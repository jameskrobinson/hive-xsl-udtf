<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method='text' encoding='iso-8859-1'/>
<xsl:strip-space elements='*' />

<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />

<!--HiveHeaders:intField:int, stringField:string -->

<xsl:template match='/*/child::*'>
<xsl:for-each select='child::*'>
<xsl:if test='position() != last()'><xsl:value-of select='normalize-space(.)'/><xsl:value-of select="$COL_SEP"/></xsl:if>
<xsl:if test='position()  = last()'><xsl:value-of select='normalize-space(.)'/><xsl:value-of select="$ROW_SEP"/>
</xsl:if>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>
		