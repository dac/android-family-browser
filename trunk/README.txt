USING THE COMMAND-LINE ANDROID SDK
==================================

We used the 'Makefile' file to build
the final package for the Android Market.

You can use the 'Makefile' to
run automated unit-tests ("make run-tests" and "make run-monkey").

You must use it to rebuild the sqlite database
every time you update the familyTree.yaml file.

Please read the 'Makefile' file for more details.
This makefile was originally written on Linux Ubuntu 9.

USING ECLIPSE
=============

You need to create two Android projects,
"FamilyBrowser" and "FamilyBrowserTest", using
File > New > Android Project:

  * Enter "Project name": FamilyBrowser
  * Turn on "Create project on existing source"
  * In "Location", use the current directory (".")
  * Be sure that the Build Target "Android 1.6" becomes selected

  * Enter "Project name": FamilyBrowserTest
  * Turn on "Create project on existing source"
  * In "Location", use the test sub-directory ("./test")
  * Be sure that the Build Target "Android 1.6" becomes selected

However, you still will need the command-line
Makefile to process the familyTree.yaml file and to
produce the sqlite database to be used by this application.

We originally used Eclipse 3.1 on MS Windows 7.

#--------------------------------------- The End
