hive-xsl-udtf
============

Transform XML into tabular data dynamically in Hive.

Note, this project is a fork of hive-jq-udtf, which is Copyright (c) CyberAgent, Inc. All Rights Reserved.

The below is a very brief introduction. For a full set of worked example and more information, please refer to:

https://github.com/jameskrobinson/hive-xsl-udtf/wiki/Hive-XSL-UDTF-Home

Installation
------------

1. Build jar with maven and install `target/hive-xsl-udtf-$VERSION.jar` into your `hive.aux.jars.path`.

   ```
   mvn clean package
   ```
2. CREATE FUNCTION

   ```sql
   CREATE FUNCTION xsltable AS 'uk.co.lowquay.hive.udtf.xsl.xslUDTF';
   ```
See [Deploying Jars for User Defined Functions and User Defined SerDes](https://cwiki.apache.org/confluence/display/Hive/HivePlugins#HivePlugins-DeployingJarsforUserDefinedFunctionsandUserDefinedSerDes) section of the official Hive documentation for more deployment details.

### Requirements

* Java 1.8
* [Apache Hive](https://hive.apache.org/) >= 1.1.0
* Saxon version 9.x
* XSL stylesheets and / or XSD templates that correspond to your XML

Usage
-----

###Basic usage:

* `xsltable(XML, XSL_FILE_PATH, PARAM_1...PARAM_N`
* The UDTF will execute the transformation in the XSL_FILE_PATH file for each incoming row of XML and return a single consolidated results set.
* XSL_FILE_PATH must be local, eg /home/user/xslfile.xsl
* Parameters are optional, and are passed to the XSL template. They are specific as 'PARAM=VALUE'
* There are three parameters which affect the functional of the UDTF:

ROW_SEP - this lets a user specify how the UDTF should delimit results. Default is NEWLINE;
COL_SEP - this lets a user specify how the UDTF should delimit field. Default is TAB;
OUTPUT_FORMAT - this lets a user specify Saxon's output format; Default is text;

Example:
````
select 
   xsltable(xml_text,
   '/home/james_k_robinson/xsl/generic_xml_to_csv.xsl', 
   'COL_SEP=,',
   'NODE_LEVEL=EVENT',
   'DEEP_COPY=NO') 
from 
   summit.trade_xml t 
where
   t.trade_id = '123456LQ'
````

In this case, NODE_LEVEL and DEEP_COPY are passed to the XSL template.

Note that `XSL_FILE_PATH` must be a constant - you can't pass this in as a parameter (yet!)

###XSD Usage:

* `xsltable(XML, XSD_FILE_PATH, PARAM_1...PARAM_N`
* As above, except the UDTF will pre-process an XSD file and auto-generate XSL. See full details of this process here:

https://github.com/jameskrobinson/hive-xsl-udtf/wiki/Example-5.-Dynamic-schema-on-read

````
select 
   xsltable(xml_text,
   '/home/james_k_robinson/xsd/iso20022/camt.053.001.02.xsd', 
   'NODE_LEVEL=Ntry',
   'NODE_TYPE=ReportEntry2', 
   'DEEP_COPY=NO')
from 
   iso.camt53_xml;
````

### Using lateral views

> Lateral view is used in conjunction with user-defined table generating functions such as explode(). [...]
> A lateral view first applies the UDTF to each row of base table and then joins resulting output rows to the input rows to form a virtual table having the supplied table alias. &mdash; <cite>[Hive Language Manual, Lateral View][1]</cite>

#### Example
````
select 
   t.*,
   x.* 
from 
   summit.trade_xml t 
   LATERAL VIEW xsltable(xml_text,'/home/james_k_robinson/xsl/EVENT_to_csv.xsl') x 
   where t.trade_id = '123456LQ' and x.asset_pors = 'S';
````

###Data conversion errors

The UDTF will attempt to handle any errors in retrieving / coverting data gracefully. An error count and error narrative is returned per row, in columns  udtfrowstatus and udtfrownarrative.
If udtfrowstatus = 0, no errors have occurred.

###Column headers and types
Column hearder and types are specified in the XLT file, as a comment. See example below - this format must be adhered to.

`<!--HiveHeaders:TradeId:string, dmOwnerTable:string, OptionOwner:string, ExpStyle:string, ExpDate:string, FirstExpDate:string, Notes:string, NoticePeriod:string -->`

If no HiveHeader string is found, all fields are returned in a single string column, called col1.


### Supported Hive types

* `int`, `bigint`, `float`, `double`, `boolean`, `string`

License
-------

All original code copyright (c) CyberAgent, Inc. All Rights Reserved.
XSL templates, XSL unit tests and XSL processing extentions to the original code copyright (c) James Robinson. All Rights Reserved.

[The Apache License, Version 2.0](LICENSE)

[1]: https://cwiki.apache.org/confluence/display/Hive/LanguageManual+LateralView
