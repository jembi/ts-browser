<?php
require_once("http://localhost:8080/JavaBridge/java/Java.inc");

use org\jembi\rhea\TerminologyServiceFactory as JTerminologyServiceFactory;

$factory = new JTerminologyServiceFactory();
$interface = $factory->getDefaultInstance();

$term = $interface->getTerm($_GET["conceptCode"], (int)$_GET["namespaceId"]);
$namespace = $term->namespace;

echo "{";
echo "\"code\": \"$term->code\", ";
echo "\"name\": \"$term->name\", ";
echo "\"namespace\": \"$namespace->name\", ";
echo "\"preferred\": \"$term->preferredTerm\", ";

echo "\"synonyms\": [";
$syns = $term->synonyms;
$first = true;
foreach ($syns as $syn) {
	if (!$first)
		echo ",";
	echo "{ \"name\": \"$syn->name\" }";
	$first = false;
}
echo " ], ";

echo "\"properties\": [";
$props = $term->properties;
$first = true;
foreach ($props as $prop) {
	if (!$first)
		echo ",";
	echo "{ \"name\": \"$prop->name\", \"value\": \"$prop->value\" }";
	$first = false;
}
echo " ] ";

echo " }";
?>
