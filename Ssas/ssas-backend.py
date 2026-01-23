from flask import Flask, request, jsonify
from flask_cors import CORS
import pyadomd
from pyadomd import Pyadomd

app = Flask(__name__)
CORS(app)  # Enable CORS for React frontend

# SSAS Connection Configuration
SSAS_CONNECTION_STRING = "Provider=MSOLAP;Data Source=your-server;Initial Catalog=your-database;"

def get_connection():
    """Create and return SSAS connection"""
    return Pyadomd(SSAS_CONNECTION_STRING)

@app.route('/api/cubes', methods=['GET'])
def get_cubes():
    """Get list of available cubes"""
    try:
        with get_connection() as conn:
            query = """
            SELECT [CUBE_NAME] 
            FROM $SYSTEM.MDSCHEMA_CUBES 
            WHERE CUBE_SOURCE = 1
            """
            with conn.cursor().execute(query) as cursor:
                cubes = [row[0] for row in cursor.fetchall()]
            return jsonify({'cubes': cubes})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/cube/<cube_name>/metadata', methods=['GET'])
def get_cube_metadata(cube_name):
    """Get dimensions and measures for a specific cube"""
    try:
        with get_connection() as conn:
            # Get dimensions
            dim_query = f"""
            SELECT DISTINCT [DIMENSION_UNIQUE_NAME]
            FROM $SYSTEM.MDSCHEMA_DIMENSIONS
            WHERE [CUBE_NAME] = '{cube_name}'
            AND [DIMENSION_IS_VISIBLE] = TRUE
            """
            with conn.cursor().execute(dim_query) as cursor:
                dimensions = [row[0] for row in cursor.fetchall()]
            
            # Get measures
            measure_query = f"""
            SELECT [MEASURE_UNIQUE_NAME]
            FROM $SYSTEM.MDSCHEMA_MEASURES
            WHERE [CUBE_NAME] = '{cube_name}'
            AND [MEASURE_IS_VISIBLE] = TRUE
            """
            with conn.cursor().execute(measure_query) as cursor:
                measures = [row[0] for row in cursor.fetchall()]
            
            return jsonify({
                'dimensions': dimensions,
                'measures': measures
            })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/query', methods=['POST'])
def execute_query():
    """Execute MDX query based on user selections"""
    try:
        data = request.json
        cube_name = data.get('cube')
        rows = data.get('rows', [])
        columns = data.get('columns', [])
        measures = data.get('measures', [])
        
        if not cube_name or not measures:
            return jsonify({'error': 'Cube and at least one measure required'}), 400
        
        # Build MDX query
        mdx_query = build_mdx_query(cube_name, rows, columns, measures)
        
        # Execute query
        with get_connection() as conn:
            with conn.cursor().execute(mdx_query) as cursor:
                # Get column headers
                column_headers = [desc[0] for desc in cursor.description]
                
                # Fetch data
                result_data = []
                for row in cursor.fetchall():
                    result_data.append([str(cell) if cell is not None else '' for cell in row])
                
                return jsonify({
                    'columnHeaders': column_headers,
                    'measureHeaders': measures,
                    'data': result_data,
                    'mdx': mdx_query
                })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

def build_mdx_query(cube_name, rows, columns, measures):
    """Build MDX query from user selections"""
    
    # Build SELECT clause with measures
    select_measures = "{" + ", ".join(measures) + "}"
    
    # Build columns axis
    if columns:
        columns_set = " * ".join([f"{col}.Members" for col in columns])
        columns_clause = f"{{{columns_set}}}"
    else:
        columns_clause = "{}"
    
    # Combine measures with columns
    if columns:
        on_columns = f"NON EMPTY ({select_measures} * {columns_clause}) ON COLUMNS"
    else:
        on_columns = f"NON EMPTY {select_measures} ON COLUMNS"
    
    # Build rows axis
    if rows:
        rows_set = " * ".join([f"{row}.Members" for row in rows])
        on_rows = f"NON EMPTY {{{rows_set}}} ON ROWS"
    else:
        on_rows = ""
    
    # Construct full MDX query
    mdx = f"""
    SELECT 
        {on_columns}
        {', ' + on_rows if on_rows else ''}
    FROM [{cube_name}]
    """
    
    return mdx.strip()

@app.route('/api/hierarchy/<cube_name>/<dimension_name>', methods=['GET'])
def get_hierarchy_members(cube_name, dimension_name):
    """Get members of a specific dimension hierarchy"""
    try:
        with get_connection() as conn:
            query = f"""
            SELECT [MEMBER_UNIQUE_NAME], [MEMBER_CAPTION]
            FROM $SYSTEM.MDSCHEMA_MEMBERS
            WHERE [CUBE_NAME] = '{cube_name}'
            AND [DIMENSION_UNIQUE_NAME] = '{dimension_name}'
            LIMIT 100
            """
            with conn.cursor().execute(query) as cursor:
                members = [{'name': row[0], 'caption': row[1]} for row in cursor.fetchall()]
            return jsonify({'members': members})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, port=5000)