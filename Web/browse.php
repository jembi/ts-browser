<!DOCTYPE html>

<?php 
require_once("http://localhost:8080/JavaBridge/java/Java.inc");

use org\jembi\rhea\TerminologyServiceFactory as JTerminologyServiceFactory;

$factory = new JTerminologyServiceFactory();
$interface = $factory->getDefaultInstance();
if (isset($_GET["namespaceId"]))
	$namespaceId = (int)$_GET["namespaceId"];
else
	$namespaceId = 32779; #let's just hope that this is set
$namespace = $interface->lookupNamespace($namespaceId);
$root = $namespace->roots[0];
?>

<html lang="en">
<head>
<meta charset="utf-8">
<title>Rwanda Terminology Service - Browse <?php echo $namespace->name; ?></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<!-- Le styles -->
<link href="css/bootstrap.css" rel="stylesheet">
<style type="text/css">
body {
	padding-top: 60px;
	padding-bottom: 40px;
}
</style>
<link href="css/bootstrap-responsive.css" rel="stylesheet">
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="images/favicon.ico">
<link rel="apple-touch-icon" href="images/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png">
</head>
<body>
<div class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container"> <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span> </a> <a class="brand" href="home.php">Rwanda Terminology Service</a>
      <div class="nav-collapse">
        <ul class="nav">
          <li><a href="home.php">Home</a></li>
          <li><a href="search.php">Search</a></li>
          <li class="active"><a href="browse.php">Browse</a></li>
        </ul>
        <ul class="nav pull-right">
          <li><a href="about.php">About</a></li>
          <li><a href="links.php">Links</a></li>
          <!--li><a href="#contact">Contact</a></li-->
        </ul>
			<form class="navbar-search pull-left" action="results.php" method="post">
			  <input type="text" class="search-query span2" placeholder="Search" name="query">
			</form>
      </div>
      <!--/.nav-collapse -->
    </div>
  </div>
</div>
<div class="container">
	<div class="row"><div class="span8" style="margin-bottom:30px"><h1><?php echo $namespace->name; ?></h1>
	<a class="btn btn-success btn-mini" href="export/<?php echo $namespace->id ?>.csv">Export as CSV &raquo</a></div>
	</div>
	<div class="row">
		<div class="span2" style="margin-bottom:30px">
			<ul class="nav nav-list">
<?php 
$namespaces = $interface->getAllNamespaces();

foreach ($namespaces as $entry) {
	if ((string)$entry->id===(string)$namespaceId)
    	echo "<li class=\"active\">";
	else
    	echo "<li>";
	echo "<a href=\"browse.php?namespaceId=$entry->id\">$entry->code</a>";
    echo "</li>";
}
?>
				<li><img id="loader" src="img/loaderB16.gif" style="display: none;" /></li>
			</ul>
		</div>
		<div class="span4 well" style="margin-bottom:30px"><ul id="treeRoot"></ul></div>
		<div class="span4" style="margin-bottom:30px"><div id="termDetails" class="well" style="position: absolute; top: 150px; display: none;"></div></div>
	</div>
    <hr>
  <footer>
    <p>&copy; Jembi Health Systems 2012</p>
  </footer>
</div>
<!-- /container -->
<!-- Le javascript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="js/jquery.js"></script>
<script src="js/bootstrap-transition.js"></script>
<script src="js/bootstrap-alert.js"></script>
<script src="js/bootstrap-modal.js"></script>
<script src="js/bootstrap-dropdown.js"></script>
<script src="js/bootstrap-scrollspy.js"></script>
<script src="js/bootstrap-tab.js"></script>
<script src="js/bootstrap-tooltip.js"></script>
<script src="js/bootstrap-popover.js"></script>
<script src="js/bootstrap-button.js"></script>
<script src="js/bootstrap-collapse.js"></script>
<script src="js/bootstrap-carousel.js"></script>
<script src="js/bootstrap-typeahead.js"></script>

<script type="text/javascript">
var namespaceId = <?php echo $namespace->id; ?>;
var itemClosedImg = "img/treeRightTriangleBlack.png";
var itemOpenImg = "img/treeDownTriangleBlack.png";
var termDetailsY = null;
var selectedCode = "";
var loadedTermDetails = false;

function loadTermDetails(code) {
	$.getJSON(
		"termjson.php?conceptCode=" + code + "&namespaceId=" + namespaceId,
		function(json) {
			$("#termDetails").empty();
			$("#termDetails").append(
				"<h3>" + json["name"] + "</h3>" +
				"<dl>" +
				"  <dt>Code</dt><dd>" + json["code"] + "</dd>" +
				"  <dt>Preferred Term</dt><dd>" + json["preferred"] + "</dd>" +
				"  <dt>Synonyms</dt>"
			);

			for (s in json["synonyms"]) {
				$("#termDetails").append(
					"<dd>" + json["synonyms"][s]["name"] + "</dd>"
				);
			}

			$("#termDetails").append("<dt>Properties</dt>");
			for (p in json["properties"]) {
				$("#termDetails").append(
					"<dd>" + json["properties"][p]["name"] + " - " + json["properties"][p]["value"] + "</dd>"
				);
			}

			$("#termDetails").append("</dl>");
			$("#termDetails").show();
		}
	);
}

function loadData(list, code) {
	$("#loader").show();
	$.getJSON(
		"subconceptsjson.php?conceptCode=" + code + "&namespaceId=" + namespaceId,
		function(json){
			for (c in json.data) {
				var scode = idFriendly(json.data[c]["code"]);
				var img = "";
				if (json.data[c]["hasSubConcepts"])
					img = "<img id=\"img" + scode + "\" src=\"" + itemClosedImg + "\" />&nbsp;";
				$(list).append("<li><div id=\"" + scode + "\" style=\"cursor: pointer;\">" + img + "<span>" + json.data[c]["name"] +
					"</span></div><div id=\"uldiv" + scode + "\"><ul id=\"ul" + scode + "\"></ul></div></li>");

				var clickFunc = function(jsoncode, acode) {
					return function() {
						if ($("#img" + acode)[0]) {
							if ($("#img" + acode).attr("src") == itemClosedImg)
								$("#img" + acode).attr("src", itemOpenImg);
							else
								$("#img" + acode).attr("src", itemClosedImg);

							if ($("#loaded" + acode)[0]) {
								$("#uldiv" + acode).slideToggle();
							} else {
								loadData("#ul" + acode, jsoncode);
								$("#" + acode).append("<div id=\"loaded" + acode + "\" style=\"display: none\" />");
							}
						}

						$("#" + selectedCode).css("font-weight", "");
						$("#" + acode).css("font-weight", "bold");
						selectedCode = acode;
						loadTermDetails(jsoncode);
					}
				};
				$("#" + scode).click(clickFunc(json.data[c]["code"], scode));
				$(list).css("list-style-type", "none");
			}

			$("#loader").hide();
		}
	);
}

function idFriendly(s) {
	return s.replace(/[\.\[\]\(\)]/g, "_");
}

$(document).ready(function(){
	var root = <?php echo "\"$root->code\""; ?>;
	loadData("#treeRoot", root);

	termDetailsY = parseInt($("#termDetails").css("top").substring(0,$("#termDetails").css("top").indexOf("px")))  
    $(window).scroll(function () {  
        var offset = termDetailsY + $(document).scrollTop() + "px";  
        $("#termDetails").animate({top: offset}, {duration: 500, queue: false});  
    });  
});
</script>
</body>
</html>
