<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method='text' encoding='UTF-8'/>
<xsl:strip-space elements='*' />

<xsl:param name='COL_SEP' select='"&#09;"' />
<xsl:param name='ROW_SEP' select='"&#10;"' />
<xsl:param name="sep" select="'.'"/> <!-- this is used when appending name field values -->

<!--HiveHeaders:namefield:string, valuefield:string-->
    
    <xsl:template match="text()"/>

    <xsl:template match="*">
        <xsl:variable name="path">
            <xsl:for-each select="ancestor-or-self::*">
                <xsl:variable name="predicate">
                    <xsl:call-template name="genPredicate"/>
                </xsl:variable>
                <xsl:if test="ancestor::*">
                    <xsl:value-of select="$sep"/>
                </xsl:if>
                <xsl:value-of select="concat(local-name(),$predicate)"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:for-each select="@*">
            <xsl:value-of select="concat($path,$sep,name(),$COL_SEP,.)"/>
            <xsl:value-of select="$ROW_SEP"/>
        </xsl:for-each>
		
		<xsl:for-each select=".">
            <xsl:if test="node() and text()"> <!-- note, this will exclude empty nodes -->
            	<xsl:value-of select="concat($path,$COL_SEP,text())"/> 
            	<xsl:value-of select="$ROW_SEP"/>
        	</xsl:if>
        </xsl:for-each>
        
        <xsl:apply-templates select="node()"/>
    </xsl:template>

    <xsl:template name="genPredicate">
        <xsl:if test="preceding-sibling::*[local-name()=local-name(current())] or following-sibling::*[local-name()=local-name(current())]">
            <xsl:value-of select="concat('[',count(preceding-sibling::*[local-name()=local-name(current())])+1,']')"/>          
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>			
