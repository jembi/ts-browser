Jembi Terminology Service Browser
=================================

The _ts-browser_ is a web front-end to the [Apelon DTS Terminology Service](http://apelon-dts.sourceforge.net/).

[Screenshots](https://github.com/jembi/ts-browser/wiki/Screenshots)

Overview
--------
The ts-browser was created by Jembi Health Systems as part of the RHEA project in Rwanda.
It was motivated by the need for a publicly accessible, vendor-neutral and customisable web-based browser for the terminology service (TS) component.

For more information on RHEA, see
* http://rhea.jembi.org
* http://www.jembi.org
* https://jembiprojects.jira.com/wiki/display/RHEAPILOT/Terminology+Service
* http://www.jembi.org/project/rwanda-health-enterprise-architecture-rhea/

Features
--------
* A terminology browser
* Searching (note that searching by code is still outstanding)
* CSV exports of terminology spaces
* Code validation Restful web service

Project Structure
-----------------
The browser consists of two components, the API and the Web UI code. The API contains a Java interface to the TS, while the Web folder contains the actual website code.

The website itself is written in PHP using the Twitter Bootstrap Framework. It interfaces to a Java app server which in turn interfaces to Apelon DTS via the API.
The interface between web and Java app server is accomplished using PHP/Java Bridge.

* http://twitter.github.com/bootstrap/
* http://php-java-bridge.sourceforge.net/pjb/

Ubuntu Installation
-------------------
The following guide is for installation on Ubuntu using Apache and Apache Tomcat.
The project has been known to run fine on Windows and Mac OSX systems,
so if you do install it on there, feel free to contribute an install guide!

*	If not already done, first install Apelon DTS http://apelon-dts.sourceforge.net/
	(see also this guide for installing on Ubuntu https://jembiprojects.jira.com/wiki/display/RHEAPILOT/Apelon+DTS+3.5+Installation).
	Note that this API has only been tested against DTS version 3.5.
*	Install both Apache and Tomcat

	```
	sudo apt-get install apache2 tomcat7
	```

*	Install PHP

	```
	sudo apt-get install php5 libapache2-mod-php5 php5-cgi
	```

	Edit the file /etc/php5/apache2/php.ini, setting On to the option allow_url_include:

	```
	allow_url_include = On
	```

	This is necessary in order to use the PHP/Java bridge in PHP.

*	Install PHP/Java Bridge

	Download the war file available via http://php-java-bridge.sourceforge.net/pjb/download.php
	Rename the file JavaBridge.war if it isn't already named so
	Copy the war file to /var/lib/tomcat7/webapps/

*	Setup the website

	Copy the Web code to /var/www/, e.g. sudo cp -R Web /var/www/tsbrowser
	Create a directory called export and make sure that the user tomcat7 has read/write access to it, i.e.

	```
	sudo chown tomcat7:tomcat7 export
	```

	This folder will be used by the API layer to cache csv exports in.

*	Compile the API code by running Ant. Copy the compiled jar to /usr/share/tomcat7/lib to make it available to the PHP/Java bridge

*	Copy the jars in the directories lib and lib-common of the API source to /usr/share/tomcat7/lib, EXCEPT for the jar servlet-api.jar (it will conflict with the existing one in the directory).

*	Restart both Apache and Tomcat

	```
	sudo /etc/init.d/apache2 restart
	sudo /etc/init.d/tomcat7 restart
	```

The TS website should now be running.

Note that when you startup the server for the first time, the csv exports will not have been generated yet. This will however be done automatically, but may take a while.

A note on security: The website only needs to be accessed externally by clients through Apache (port 80). So it is not necessary to open ports for Apelon DTS or Tomcat on the server.

Web Service
-----------
The following service is exposed by the browser:

```
http://server/validate.php?namespaceCode=CODE&conceptCode=CODE
```

An example might be:

```
http://yourserver/validate.php?namespaceCode=ICD10RW&conceptCode=A00.0
```

The resulting xml will look as follows on success:

```
<tsvalidate>
  <result>1</result>
</tsvalidate>
```

and will return a 0 if the code is invalid.

There's also

```
<tsvalidate>
  <result>Invalid Request</result>
</tsvalidate>
```

and

```
<tsvalidate>
  <result>Exception</result>
  <details>
	...
  </details>
</tsvalidate>
```

if something goes wrong.

Future Work
-----------
Although currently implemented as an Apelon DTS front-end, this software should be enhanced to support
other terminology services. New services could easily be supported by adding code level support for them,
but ideally we would like to use a standard such as the Common Terminology Services (CTS) for communication.

http://www.hl7.org/implement/standards/product_brief.cfm?product_id=10
