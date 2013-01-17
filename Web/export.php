<!--
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this
 file, You can obtain one at http://mozilla.org/MPL/2.0/.
-->
<?php
require_once("http://localhost:8080/JavaBridge/java/Java.inc");

header('Content-disposition: attachment; filename=export.csv');
header('Content-type: text/csv');

use org\jembi\rhea\TerminologyServiceFactory as JTerminologyServiceFactory;

$factory = new JTerminologyServiceFactory();
$interface = $factory->getDefaultInstance();
if (isset($_GET["conceptCode"]))
	echo $interface->exportTerm($_GET["conceptCode"], (int)$_GET["namespaceId"]);
?>
