<?php
require_once("http://localhost:8080/JavaBridge/java/Java.inc");

use org\jembi\rhea\TerminologyServiceFactory as JTerminologyServiceFactory;

$factory = new JTerminologyServiceFactory();
$interface = $factory->getDefaultInstance();

$term = $interface->getTerm($_GET["conceptCode"], (int)$_GET["namespaceId"]);

echo "{ \"data\": [";

$first = true;

foreach ($term->getSubConcepts() as $sub) {
	if ($first)
		echo "{";
	else
		echo ",{";
	echo "\"code\": \"$sub->code\", ";
	echo "\"name\": \"$sub->name\", ";
	echo "\"hasSubConcepts\": $sub->hasSubConceptsAsString";
	echo "} ";

	$first = false;
}

echo " ] }";
?>
