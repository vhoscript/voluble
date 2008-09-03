<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" omit-xml-declaration="yes"/>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

    <!--  want no new-lines in this next section, because they would show up in the output file! -->
    <!--  also, double quotes are reduced to single quotes because we use message format to insert versions -->
    <xsl:template match="/*[local-name()=''project'']/*[local-name()=''version'']/text()[contains(.,''{0}'')]">{1}</xsl:template>
 
</xsl:stylesheet>

