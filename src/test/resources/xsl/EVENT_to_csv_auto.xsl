<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
<xsl:strip-space elements='*' />
<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />
<xsl:param name='DEEP_COPY' select='"YES"' />
			<!--HiveHeaders:Date:string, ADate:string, LinkEv:string, Type:string, Ccy:string, Amount:string, RiskId:string, EvStyle:string, BDate:string, CDate:string, EventCal:string, EventBDRule:string -->
 		
	
	<xsl:template match="@*|node()">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
			
    <xsl:template match="EVENT">
<xsl:value-of select="Date"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="ADate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="LinkEv"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Type"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Ccy"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="Amount"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="RiskId"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="EvStyle"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="BDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="CDate"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="EventCal"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="EventBDRule"/><xsl:value-of select="$COL_SEP"/>
<xsl:value-of select="$ROW_SEP"/>
</xsl:template>
	
	<xsl:template name="deep-copy">
		<xsl:param name="field-name"/>    	
		<xsl:choose>
			<xsl:when test = "./$field-name">
				<xsl:if test="$DEEP_COPY='YES'">	
					<xsl:call-template name="serializeNodeToString">
						<xsl:with-param name="node" select = "./$field-name"/>
					</xsl:call-template>
				</xsl:if>				
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

		