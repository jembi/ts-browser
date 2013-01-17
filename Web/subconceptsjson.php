<!--
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this
 file, You can obtain one at http://mozilla.org/MPL/2.0/.
-->
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
