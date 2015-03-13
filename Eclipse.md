

# Using Eclipse for Android #

See also [Eclipse for Android](http://coding.smashingmagazine.com/2011/11/04/getting-the-best-out-of-eclipse-for-android-development) development


---

## Requirements ##

Install the following packages (or newer versions) :
  * Java SDK 6
  * Java Ant 1.8
  * Android [SDK](http://developer.android.com/sdk/index.html) release 20
  * Android [NDK](http://developer.android.com/tools/sdk/ndk/index.html) release 8
  * [Eclipse](http://www.eclipse.org) Juno release
  * Doxygen
  * Python 2

Use `bash share/tools/install_android_sdk_on_debian.bash` to install some of them on Debian, Ubuntu or LinuxMint

You also need to have run a build using `bash b debug` (or `bash b release`) at least once, in order to process some template files needed by Eclipse.
For example, all the `AndroidManifest.xml` files are generated from files under `templates/`


---

## Eclipse Plugins ##

Import file `share/eclipse/bookmarks.xml` at `Help > Install New Software... > Available Software Sites > Import...`

Install the following Eclipse plugins at `Help > Install New Software... > Add` :

  * [ADT Plugin](https://dl-ssl.google.com/android/eclipse/): Android Development Tools: see http://developer.android.com/sdk/eclipse-adt.html

  * [Checkstyle Plugin](http://eclipse-cs.sf.net/update/): see http://eclipse-cs.sourceforge.net/downloads.html
  * [FindBugs Plugin](http://findbugs.cs.umd.edu/eclipse/): see http://findbugs.cs.umd.edu/eclipse/
  * [PMD Plugin](http://pmd.sf.net/eclipse): see http://pmd.sourceforge.net/integrations.html#eclipse
  * [UCDetector Plugin](http://ucdetector.sourceforge.net/update): Unused Code Detector: see http://www.ucdetector.org/

Other suggested plugins:
  * At [ist.berkeley.edu](http://ist.berkeley.edu/as-ag/tools/howto/eclipse-plugins.html)
  * At [andrei.gmxhome.de](http://andrei.gmxhome.de/eclipse.html)
  * At [Wakaleo](http://www.wakaleo.com/component/content/article/199)


---

## Eclipse Android Preferences ##

In `Window > Preferences > Android` :
  * Enter `SDK Location:`

In `Window > Preferences > Android > Native Development` :
  * Enter `NDK Location:`


---

## Eclipse Project Explorer ##

Create next projects under `Project Explorer` :

  * `ca_chaves_android`
  * `ca_chaves_familyBrowser_app`
  * `ca_chaves_familyBrowser_main`
  * `ca_chaves_familyBrowser_test`

using `File > New > Project... > Android > Android Project` :

  * Enter `Project name`: `ca_chaves_android`
    * Turn on `[X] Create project from existing source`
    * In `Location`, browse to the `android` directory (`./android`)
    * Be sure that `Build Target`: `[X] Android 1.6` is selected
    * Turn on `[X] Is Library`
    * Enter `Application Name`: `ca_chaves_android`
    * Enter `Minimum SDK`: `4`

  * Enter `Project name`: `ca_chaves_familyBrowser_app`
    * Turn on `[X] Create project from existing source`
    * In `Location`, browse to the `app` directory (`./app`)
    * Be sure that `Build Target`: `[X] Android 1.6` is selected
    * Turn on `[X] Is Library`
    * Enter `Application Name`: `ca_chaves_familyBrowser_app`
    * Enter `Minimum SDK`: `4`

  * Enter `Project name`: `ca_chaves_familyBrowser_main`
    * Turn on `[X] Create project from existing source`
    * In `Location`, browse to the `main` directory (`./main`)
    * Be sure that `Build Target`: `[X] Android 1.6` is selected
    * Enter `Application Name`: `Family Browser`
    * Enter `Minimum SDK`: `4`

  * Enter `Project name`: `ca_chaves_familyBrowser_test`
    * Turn on `[X] Create project from existing source`
    * In `Location`, browse to the `test` directory (`./test`)
    * Be sure that `Build Target`: `[X] Android 1.6` is selected
    * Enter `Application Name`: `Family Browser Test`
    * Enter `Minimum SDK`: `4`

You have to update these projects:

  * Right click on `Package Explorer > ca_chaves_android`, select `Properties`
    * Enter `Properties > Java Compiler` :
      * Turn on `[X] Enable project specific settings`
      * Edit `Compile compliance level`: `1.6`

  * Right click on `Package Explorer > ca_chaves_familyBrowser_app`, select `Properties`
    * Enter `Properties > Java Compiler` :
      * Turn on `[X] Enable project specific settings`
      * Edit `Compile compliance level`: `1.6`
    * Enter Java Build Path > Projects > Required projects on the build path :
      * `ca_chaves_android`

  * Right click on `Package Explorer > ca_chaves_familyBrowser_main`, select `Properties`
    * Enter `Properties > Java Compiler` :
      * Turn on `[X] Enable project specific settings`
      * Edit `Compile compliance level`: `1.6`
    * Enter Java Build Path > Projects > Required projects on the build path :
      * `ca_chaves_android`
      * `ca_chaves_familyBrowser_app`

  * Right click on `Package Explorer > ca_chaves_familyBrowser_test`, select `Properties`
    * Enter `Properties > Java Compiler` :
      * Turn on `[X] Enable project specific settings`
      * Edit `Compile compliance level`: `1.6`
    * Enter `Properties > Java Build Path > Projects > Required projects on the build path` :
      * `ca_chaves_android`
      * `ca_chaves_familyBrowser_app`
      * `ca_chaves_familyBrowser_main`


---

## Eclipse Java Code Style ##

You might also want to import the following files:

  * `Window > Preferences > Java > Code Style > Clean Up > Import` :
    * Load file `share/eclipse/java.codeStyle.cleanUp.profile.xml`

  * `Window > Preferences > Java > Code Style > Code Templates > Import` :
    * Load file `share/eclipse/java.codeStyle.codeTemplates.profile.xml`

  * `Window > Preferences > Java > Code Style > Formatter > Import` :
    * Load file `share/eclipse/java.codeStyle.formatter.profile.xml`

  * `Window > Preferences > Java > Code Style > Organize Import > Import` :
    * Load file `share/eclipse/java.codeStyle.importorder`


---

## Eclipse Java Compiler Preferences ##

In `Window > Preferences > Java > Compiler > Building` :
  * `Maximum number of problems reported per compilation unit: 10000`


---

## Eclipse Javadoc Preferences ##

In `Window > Preferences > Java > Compiler > Javadoc` :
  * `[X] Process Javadoc comments`
  * `Malformed Javadoc comments: Warning`
    * `Only consider members as visible as: Protected`
    * `[X] Validate tag arguments`
      * `[ ] Report non visible references`
      * `[ ] Report deprecated references`
    * `Missing tag description: Validate all standard tags`
  * `Missing Javadoc tags: Warning`
    * `Only consider members as visible as: Protected`
    * `[X] Ignore in overriding and implementing methods`
    * `[X] Ignore method type parameters`
  * `Missing Javadoc comments: Ignore`


---

## Eclipse XML Preferences ##

In `Window > Preferences > XML Files > Editor` :
  * `Line width: 72`
  * `[X] Split multiple attributes each on a new line`
  * `[X] Align final bracket in multi-line element tags`
  * `[X] Preserve whitespace in tags with PCDATA content`
  * `[ ] Clear all blank lines`
  * `[X] Format comments`
    * `[X] Join lines`
  * `[X] Insert whitespace before closing empty end-tags`
  * `[ ] Indent using tabs`
  * `[X] Indent using spaces`
  * `Indentation size: 4`
  * `[X] Use inferred grammar in absence of DTD/Schema`


---

