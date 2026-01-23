# SSAS Cube Browser - Setup Instructions

## Backend Setup (Python/Flask)

### 1. Install Dependencies

Create a `requirements.txt` file:
```
Flask==3.0.0
flask-cors==4.0.0
pyadomd==1.0.0
```

Install packages:
```bash
pip install -r requirements.txt
```

### 2. Configure Connection String

Edit the Flask app (`app.py`) and update the connection string:

```python
SSAS_CONNECTION_STRING = "Provider=MSOLAP;Data Source=your-ssas-server:port;Initial Catalog=your-database;User ID=username;Password=password;"
```

**Connection string formats:**

- **Windows Authentication:**
  ```
  Provider=MSOLAP;Data Source=server-name;Initial Catalog=database-name;Integrated Security=SSPI;
  ```

- **SQL Server Authentication:**
  ```
  Provider=MSOLAP;Data Source=server-name;Initial Catalog=database-name;User ID=username;Password=password;
  ```

- **Azure Analysis Services:**
  ```
  Provider=MSOLAP;Data Source=asazure://region.asazure.windows.net/servername;Initial Catalog=database-name;User ID=username;Password=password;
  ```

### 3. Run Backend

```bash
python app.py
```

Backend will run on `http://localhost:5000`

---

## Frontend Setup (React)

### Option 1: Using the Artifact

The React component provided runs directly in Claude's artifact viewer. Just update the `API_BASE` constant if your backend runs on a different port:

```javascript
const API_BASE = 'http://localhost:5000/api';
```

### Option 2: Standalone React App

1. Create a new React app:
```bash
npx create-react-app ssas-browser
cd ssas-browser
```

2. Install dependencies:
```bash
npm install lucide-react
```

3. Replace `src/App.js` with the React component code

4. Start the app:
```bash
npm start
```

---

## Usage Guide

### 1. Select a Cube
Choose from the dropdown of available cubes in your SSAS database.

### 2. Build Your Query
- **Drag dimensions** from the left panel to **Rows** or **Columns** drop zones
- **Drag measures** from the middle panel to **Selected Measures** zone
- You can drag items between zones or remove them with the X button

### 3. Execute Query
Click "Execute Query" to run the MDX query and view results.

### 4. Export Data
Click "Export CSV" to download the results as a CSV file.

---

## Architecture

```
┌─────────────┐         HTTP          ┌──────────────┐         MDX         ┌──────────┐
│   React     │ ◄──────────────────► │    Flask     │ ◄─────────────────► │   SSAS   │
│  Frontend   │      JSON/REST        │   Backend    │      pyadomd        │   Cube   │
└─────────────┘                       └──────────────┘                     └──────────┘
```

---

## Troubleshooting

### pyadomd Installation Issues

If you encounter issues installing `pyadomd`, try:

```bash
# Use conda if available
conda install -c conda-forge pyadomd

# Or use alternative package
pip install adodbapi
```

### CORS Errors

Make sure Flask-CORS is properly configured in the backend. The current setup allows all origins for development. For production, restrict origins:

```python
CORS(app, resources={r"/api/*": {"origins": "https://your-frontend-domain.com"}})
```

### Connection Errors

- Verify SSAS server is accessible from your Python environment
- Check firewall rules allow connections on SSAS port (default 2383)
- Ensure you have proper permissions on the SSAS database
- Test connection string using SQL Server Management Studio first

### Empty Dimensions/Measures

If no dimensions or measures appear:
- Check cube permissions
- Verify cube is processed
- Ensure CUBE_SOURCE = 1 filter matches your cube type

---

## Advanced Features to Add

Consider extending with:
- **Filters**: Add WHERE clause builder for dimension filtering
- **Hierarchies**: Browse hierarchy levels and select specific members
- **Calculated Measures**: UI for creating ad-hoc calculations
- **Query History**: Save and reload previous queries
- **Pivot Table View**: Alternative visualization of results
- **Excel Export**: Rich formatting with openpyxl
- **Chart Visualization**: Add charts using recharts library