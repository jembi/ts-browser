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
