<!DOCTYPE html>

<!--
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this
 file, You can obtain one at http://mozilla.org/MPL/2.0/.
-->

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

  <!-- Content columns -->
  <div class="row">
    <div class="span8 offset2">
    <form class="form-horizontal" action="results.php" method="post">
        <fieldset>

          <legend>Terminology Standards Search</legend>
        
          <div class="control-group">
            <label class="control-label">Term Search</label>
            <div class="controls">
            	<div class="input-append">
              <input class="span6" type="text" placeholder="e.g. Malaria" name="query" maxlength="64">
              </div>
              
            </div>
            
          </div>

          <div class="control-group">
            <div class="controls">
			  <label class="checkbox"><input type="checkbox" name="exact" value="exact">Exact match</label>
            </div>
          </div>
       
       
          <div class="control-group">
            <label class="control-label" for="optionsCheckboxList">Available Terminology Sets</label>
            <div class="controls">
<?php 
require_once("http://localhost:8080/JavaBridge/java/Java.inc");

use org\jembi\rhea\TerminologyServiceFactory as JTerminologyServiceFactory;

$factory = new JTerminologyServiceFactory();
$interface = $factory->getDefaultInstance();
$list = $interface->getAllNamespaces();
$ar = java_values ($list->toArray());

foreach ($ar as $entry) {
	echo "<label class=\"checkbox\">";
	echo "<input type=\"checkbox\" name=\"searchNamespaces[]\" value=\"$entry->id\">";
	echo "$entry->name";
	echo "</label>";
}
?>
             <p class="help-block">Select the Terminology standards you would like to see results for. Leaving this blank will search all available terminologies.</p> 
            </div>
          </div>
          
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">Search  <i class="icon-search"></i></button>
          </div>
        </fieldset>
      </form> 
    </div>
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
