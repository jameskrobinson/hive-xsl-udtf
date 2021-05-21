<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' 
xpath-default-namespace='http://www.w3.org/2001/XMLSchema' 
xmlns:xs='http://www.w3.org/2001/XMLSchema' >
    
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
	<xsl:strip-space elements='*' />

	<xsl:variable name='COL_SEP' select='"&#09;"' />
	<xsl:variable name='ROW_SEP' select='"&#10;"' />
	<xsl:param name='NODE_LEVEL' select='"BACK"' />

	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>

    <xsl:template match="/">
		<xsl:for-each select="//xs:element[@name=$NODE_LEVEL]">
			<xsl:call-template name="header"/>
			<xsl:call-template name="loop"/>	
			<xsl:call-template name="footer"/>	
		</xsl:for-each>
    </xsl:template>

	<xsl:template name="header">
      		<xsl:text disable-output-escaping = 'yes'>&lt;xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'&gt;
&lt;xsl:output method="text" omit-xml-declaration="yes" indent="no"/&gt;
&lt;xsl:strip-space elements='*' /&gt;
&lt;xsl:param name='COL_SEP' select='"<![CDATA[&]]>#09;"' /&gt;
&lt;xsl:param name='ROW_SEP' select='"<![CDATA[&]]>#10;"' /&gt;
&lt;xsl:param name='DEEP_COPY' select='"YES"' /&gt;
			</xsl:text>
			<!-- output the Hive header... in time, we'll select out datatypes here too, but for now we cheating and using strings for everything -->
			<!-- note, the below is somewhat hardcoded for Summit XSD's - it will vary slightly for other XSD's -->

			<xsl:text>&lt;!--HiveHeaders:</xsl:text>
        	<xsl:for-each select="./xs:complexType/xs:sequence/*">
            	<xsl:value-of select="attribute(name)"/>
            	<xsl:text>:string</xsl:text>
            	<!--<xsl:value-of select="attribute(type)"/> -->
            	<xsl:choose>
            		<xsl:when test="position()=last()">
						<xsl:text> --&gt;</xsl:text>
						<xsl:value-of select="$ROW_SEP"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>, </xsl:text>
					</xsl:otherwise>
            	</xsl:choose>
        	</xsl:for-each>
			<xsl:text> 		
	
	&lt;xsl:template match="@*|node()"&gt;
		&lt;xsl:apply-templates select="@*|node()"/&gt;
	&lt;/xsl:template&gt;
			</xsl:text>
    		<xsl:text>
    &lt;xsl:template match="</xsl:text><xsl:value-of select ="$NODE_LEVEL"/><xsl:text>"&gt;</xsl:text>
    	<xsl:value-of select="$ROW_SEP"/>
	
	</xsl:template>

	<xsl:template name='loop'>
		<!-- output a row per element, getting the data that corresponds to that element... -->
		<!-- if the element is a complex type containing a sequence, then it's a special case, requiding a deep copy -->
		<xsl:for-each select="./xs:complexType/xs:sequence/*">
			<xsl:choose>
	    		<xsl:when test = "./xs:complexType/xs:sequence/*">
	    			<xsl:text>&lt;xsl:call-template name="deep-copy">&lt;xsl:with-param name="field-name" select = "</xsl:text><xsl:value-of select="attribute(name)"/>
	    			<xsl:text>"/&gt;&lt;/xsl:call-template>&lt;xsl:value-of select="$COL_SEP"/&gt;</xsl:text>
					<xsl:value-of select="$ROW_SEP"/>
				</xsl:when>
				
				<xsl:otherwise>
					<xsl:text>&lt;xsl:value-of select="</xsl:text><xsl:value-of select="attribute(name)"/>
					<xsl:text>"/&gt;&lt;xsl:value-of select="$COL_SEP"/&gt;</xsl:text>
					<xsl:value-of select="$ROW_SEP"/>
				</xsl:otherwise>
			</xsl:choose>	    	
		</xsl:for-each>
		<xsl:text>&lt;xsl:value-of select="$ROW_SEP"/&gt;</xsl:text>
		<xsl:value-of select="$ROW_SEP"/>
		<xsl:text>&lt;/xsl:template&gt;</xsl:text>
	</xsl:template>
	
    <xsl:template name='footer'>
		<xsl:text disable-output-escaping = 'no'>
	
	&lt;xsl:template name="deep-copy"&gt;
		&lt;xsl:param name="field-name"/&gt;    	
		&lt;xsl:choose&gt;
			&lt;xsl:when test = "./$field-name"&gt;
				&lt;xsl:if test="$DEEP_COPY='YES'"&gt;	
					&lt;xsl:call-template name="serializeNodeToString"&gt;
						&lt;xsl:with-param name="node" select = "./$field-name"/&gt;
					&lt;/xsl:call-template&gt;
				&lt;/xsl:if&gt;				
			&lt;/xsl:when&gt;
			&lt;xsl:otherwise&gt;
			&lt;/xsl:otherwise&gt;
		&lt;/xsl:choose&gt;
    &lt;/xsl:template&gt;

	&lt;xsl:template name="serializeNodeToString"&gt;
		&lt;xsl:param name="node"/&gt;
		&lt;xsl:variable name="name" select="name($node)"/&gt;
		&lt;xsl:if test="$name"&gt;
			&lt;xsl:value-of select="concat('&amp;lt;',$name)"/&gt;
			&lt;xsl:for-each select="$node/@*"&gt;
				&lt;xsl:value-of select="concat(' ',name(),'=&amp;quot;',.,'&amp;quot; ')"/&gt;
			&lt;/xsl:for-each&gt;
			&lt;xsl:value-of select="concat('&amp;gt;',./text())"/&gt;
		&lt;/xsl:if&gt;
		&lt;xsl:for-each select="$node/*"&gt;
			&lt;xsl:call-template name="serializeNodeToString"&gt;
				&lt;xsl:with-param name="node" select="."/&gt;
			&lt;/xsl:call-template&gt;
		&lt;/xsl:for-each&gt;
		&lt;xsl:if test="$name"&gt;
			&lt;xsl:value-of select="concat('&amp;lt;/',$name,'&amp;gt;')"/&gt;
		&lt;/xsl:if&gt;
	&lt;/xsl:template&gt;

&lt;/xsl:stylesheet&gt;   

		</xsl:text>
	</xsl:template>

</xsl:stylesheet>