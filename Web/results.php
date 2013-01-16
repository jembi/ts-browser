<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Rwanda Terminology Service</title>
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
          <li class="active"><a href="search.php">Search</a></li>
          <li><a href="browse.php">Browse</a></li>
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
	<div class="row"><div class="span8 offset1" style="margin-bottom:30px"><h1>Search results for <?php echo $_POST["query"]; ?></h1></div></div>

  <div class="row">
    <div class="span8 offset2">
    <form class="form-horizontal" action="results.php" method="post">
        <fieldset>
          <div class="control-group">
            <label class="control-label">Term Search</label>
            <div class="controls">
            	<div class="input-append">
              <span><input class="span5" type="text" placeholder="e.g. Malaria" value='<?php echo $_POST["query"]; ?>' name="query">
              <button type="submit" class="btn btn-primary">Search  <i class="icon-search"></i></button></span>
              </div>
            </div>
          </div>
        </fieldset>
      </form> 
    </div>
  </div>

	<div class="row" style="margin-bottom:10px">

<?php 
require_once("http://localhost:8080/JavaBridge/java/Java.inc");
use org\jembi\rhea\TerminologyServiceFactory as JTerminologyServiceFactory;
use java\util\LinkedList as JLinkedList;

$factory = new JTerminologyServiceFactory();
$interface = $factory->getDefaultInstance();
$ids = new JLinkedList();

if (!empty($_POST["searchNamespaces"])) {
	for ($i=0; $i<count($_POST["searchNamespaces"]); $i++)
		$ids->add( (int)$_POST["searchNamespaces"][$i] );
}

$list = $interface->search($ids, $_POST["query"], isset($_POST["exact"]));
$listSize = (string)$list->size();

if ($listSize==="0") {
	echo "<div class='offset1'><small>No results found</small></div>";
} else {
	foreach ($list as $entry) {
		$ns = $entry->namespace;
		echo "<div class='offset1'><span><a href=\"term.php?code=$entry->code&ns=$ns->id\">$entry->name</a></span><br/><span>$ns->name";
		$props = $entry->properties;
		$propsArr = java_values($props->toArray());
		foreach ($propsArr as $prop) {
			echo ", $prop->name: $prop->value";
		}
		echo "</span></div><br/>\n";
	}
}
?>

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
</body>
</html>
