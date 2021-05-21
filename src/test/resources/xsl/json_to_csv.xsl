<?xml version="1.0"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:math="http://www.w3.org/2005/xpath-functions/math"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xs math" version="3.0">
	<xsl:output indent="yes" omit-xml-declaration="yes" />

	<xsl:param name="json-xml" select="json-to-xml(.)"/>
	
	<xsl:template match="data">
		<xsl:value-of select="$json-xml/*/*/*/*!string-join(*, ',')" separator="&#10;"/>	 
	</xsl:template>

</xsl:stylesheet>