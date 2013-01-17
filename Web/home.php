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
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="home.php">Rwanda Terminology Service</a>
          <div class="nav-collapse">
            <ul class="nav">
              <li class="active"><a href="home.php">Home</a></li>
              <li><a href="search.php">Search</a></li>
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
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container">

      <!-- Main hero unit for a primary use message or call to action -->
      <div class="hero-unit">
        <h1>Rwanda Terminology Service</h1>
        <p>The Rwandan Ministry of Health has published is standard sets of terminologies to be used in the Health Sector. Utilize the Rwandan Terminology Service to locate the standard codes supported by the Rwandan Ministry of Health; choose to view, or export a single or set of terms. Start your search here:</p>
		<p>
    	<form class="form-horizontal" action="results.php" method="post">
          <div class="control-group">
            <div class="controls">
            	<div class="input-append">
              <span><input class="span5" type="text" placeholder="e.g. Malaria" name="query">
              <button type="submit" class="btn btn-primary">Search  <i class="icon-search"></i></button></span>
              </div>
            </div>
          </div>
		</form>
		<a href="search.php">Advanced search</a>
		</p>
      </div>

      <!-- Content columns -->
      <div class="row">
        <div class="span6">
          <h2>Browse the Standards</h2>
           <p>The Rwanda Terminology Service houses standardized code sets including ICD-10 and LOINC and also includes a range of codeset extensions specialized to Rwanda. </p>
          <p><a class="btn" href="browse.php">Start Browsing &raquo;</a></p>
        </div>
        <div class="span6">
          <h2>Links</h2>
          <p>Links to various terminology standards.
          <p><a class="btn" href="links.php">Links &raquo;</a></p>
        </div>
      </div>

      <hr>

      <footer>
        <p>&copy; Jembi Health Systems 2012</p>
      </footer>

    </div> <!-- /container -->

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
