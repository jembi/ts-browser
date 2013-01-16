<?php
require_once("http://localhost:8080/JavaBridge/java/Java.inc");

echo "<tsvalidate>";

use org\jembi\rhea\TerminologyServiceFactory as JTerminologyServiceFactory;

try {
	$factory = new JTerminologyServiceFactory();
	$interface = $factory->getDefaultInstance();

	if (!isset($_GET["conceptCode"]) or !isset($_GET["namespaceCode"])) {
		echo "<result>Invalid request</result>";
	} else {
		$res = $interface->validateTerm_ReturnAs1or0String($_GET["conceptCode"], $_GET["namespaceCode"]);
		echo "<result>$res</result>";
	}
} catch (Exception $ex) {
	echo "\n<result>Exception</result>\n";
	echo "<details>\n";
	echo $ex;
	echo "</details>\n";
}

echo "</tsvalidate>";
?>
