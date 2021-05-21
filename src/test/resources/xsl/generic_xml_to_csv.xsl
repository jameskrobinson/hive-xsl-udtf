<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
	<xsl:strip-space elements='*' />

	<xsl:param name='COL_SEP' select='"&#09;"' />
	<xsl:param name='ROW_SEP' select='"&#10;"' />
	<xsl:param name='NODE_LEVEL' select='"ASSET"' />
	<xsl:param name='DEEP_COPY' select='"YES"' />

	<!-- note, by default this does a deep copy on any nodes that have sub-nodes -->

	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>

    <xsl:template match="/">
        <!-- Output headers - need a tweak hear to stop the header repeating!!! -->
        <xsl:for-each select="//*[name()=$NODE_LEVEL][1]/.">
        		<xsl:if test="position()=1">
        			<xsl:call-template name="header"/>
        		</xsl:if>
         </xsl:for-each>
        
        <xsl:for-each select="//*[name()=$NODE_LEVEL]">
        	<xsl:call-template name="loop"/>
    	</xsl:for-each>
    </xsl:template>

	 <xsl:template name="header">
        <xsl:text>H</xsl:text>
        <xsl:value-of select="$COL_SEP"/>
        <xsl:value-of select="./name()"/>
        <xsl:value-of select="$COL_SEP"/>
        
        <xsl:for-each select="./*">
            <xsl:value-of select="name()"/>
            <xsl:value-of select="$COL_SEP"/>
        </xsl:for-each>
		<xsl:value-of select="$ROW_SEP"/>
	</xsl:template>
    
    <xsl:template name="loop">
        <!-- Output values -->
        
        <xsl:text>D</xsl:text>
        <xsl:value-of select="$COL_SEP"/>
        <xsl:value-of select="./name()"/>
        <xsl:value-of select="$COL_SEP"/>
        
        <xsl:for-each select="./*">
        	<xsl:choose>
        		<xsl:when test = "*">
        			<!-- Process nodes having childs - do a copy instead -->
					<xsl:if test="$DEEP_COPY='YES'">	
      					<xsl:call-template name="deep-copy"><xsl:with-param name="field-name" select = "node()/.."/></xsl:call-template>
      				</xsl:if>
      			</xsl:when>
      			<xsl:otherwise>
      				<xsl:value-of select="node()"/>
        		</xsl:otherwise>
        	</xsl:choose>
        	<xsl:value-of select="$COL_SEP"/>
        </xsl:for-each> 
    	<xsl:value-of select="$ROW_SEP"/>
    </xsl:template>
	
	<xsl:template name="deep-copy">
		<xsl:param name="field-name"/>    	
		<xsl:choose>
			<xsl:when test = "./$field-name">
					<xsl:call-template name="serializeNodeToString">
						<xsl:with-param name="node" select = "./$field-name"/>
					</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:template>

	<xsl:template name="serializeNodeToString">
		<xsl:param name="node"/>
		<xsl:variable name="name" select="name($node)"/>
		<xsl:if test="$name">
			<xsl:value-of select="concat('&lt;',$name)"/>
			<xsl:for-each select="$node/@*">
				<xsl:value-of select="concat(' ',name(),'=&quot;',.,'&quot; ')"/>
			</xsl:for-each>
			<xsl:value-of select="concat('&gt;',./text())"/>
		</xsl:if>
		<xsl:for-each select="$node/*">
			<xsl:call-template name="serializeNodeToString">
				<xsl:with-param name="node" select="."/>
			</xsl:call-template>
		</xsl:for-each>
		<xsl:if test="$name">
			<xsl:value-of select="concat('&lt;/',$name,'&gt;')"/>
		</xsl:if>
	</xsl:template>
	
	
</xsl:stylesheet>