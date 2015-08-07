**Update**: On 7/23/2008 I updated this sample to work with either Apache Felix or Eclipse Equinox (I did this as a result of the comments posted at the bottom of this page).  The updated code does not rely on the Eclipse-BuddyPolicy, but instead uses DynamicImport-Package.  The original code is in SVN under http://voluble.googlecode.com/svn/trunk/osgi-samples/hibernate-equinox/

This article is [cross-posted here](http://notehive.com/wp/2008/07/23/osgi-hibernate-spring-dm-sample/).  I don't check this page very often and google does not send me an email when a comment is posted.  For a faster response to comments and questions, please post your comments [here](http://notehive.com/wp/2008/07/23/osgi-hibernate-spring-dm-sample/).

# Introduction #

osgi-samples/hibernate demonstrates how to use Hibernate in OSGi with Spring Framework and Spring-DM.

Starting Hibernate in OSGi with the normal Hibernate SessionFactory would allow you to use Hibernate in OSGi - but that would not address the dynamic behavior that an OSGi system is capable of.  This sample demonstrates how a Hibernate SessionFactory can be updated dynamically in an OSGi environment.  You can dynamically add and remove entity classes to the Hibernate configuration by stopping and starting bundles that contribute clasess to the Hibernate configuration.

This implementation contains a small Swing UI which allows you to see which entity classes are present in the Hibernate configuration.  The Swing UI also allows you to issue rudimentary SQL and HQL queries.

The solution is based on this [OSGi and Hibernate blog entry](http://www.osgi.org/blog/labels/osgi%20hibernate%20felix%20equinox%20eclipse%20knopflerfish%20sql.html) by Peter Kriens.
The design uses an entry in the manifest file to declaratively add classes to a Hibernate session using an _extender model_.

## Get the code ##

To get the source code for the hibernate sample, use SVN:

svn checkout http://voluble.googlecode.com/svn/trunk/osgi-samples/hibernate/ osgi-hibernate-sample

## Run the code ##

Run "mvn install" in the new osgi-hibernate-sample folder and see all projects build successfully.

The 2nd last project that builds is "integration-tests", which uses Spring DM to run an integration test suite in an OSGi container.  The integration tests are run against an in-process HSQLDB server.

The very last project that builds is "deployment".  This projects assembles all our project bundles and dependencies in the deployment/target/classes/ folder.  It also adds a Equinox and Felix configuration files (configuration/config.ini and felix-config/config.properties respectively) and Unix and Windows batch files to launch either framework.  The batch files let you to run either Equinox or Felix and automatically load the full set of bundles from this project in a standalone environment.

When you start the standalone OSGi server (by running either felix.cmd, equinox.cmd, felix.sh or equinox.sh in the osgi-samples/hibernate/deployment/target/classes folder), a Swing UI is displayed.  Click on the "Show Hibernate Config" button to see the classes in the configuration.

**In Equinox**: Type "ss" in the Equinox command window.  You'll see that bundles 28 and 29 are "resolved" but not active.  Start one or either by executing "start 28" and/or "start 29".

**In Felix**: Type "ps" in the Felix command window.   You'll see that bundles 1 and 2 are "installed" but not active.  Start one or either by executing "start 1" and/or "start 2".

Now go back to the Swing UI and click on "Show Hibernate Config" again to see which classes are present in the Hibernate configuration.

You can also view data by typing the HQL "`from A1`" and clicking "Execute HQL" (note that "`from A1`" is roughly the HQL equivalent of the SQL statement "`select * from A1`").  You should see "`Rows returned: 0`".

To add data to the database, enter the SQL statement "`insert into A1 values (1, 'value A','value B')`" and press the "Execute SQL" button.  Then run the HQL query "from A1" again to see your data being retrieved using Hibernate.  (You can also connect to the Database from a different tool (like DbVisualizer) using using the URL "jdbc:hsqldb:hsql://localhost/osgi-hibernate-sample".  The username should be "sa" and the password should be empty).

## Understanding the code ##

With an OSGi application you can dynamically add, start, stop and remove bundles from the system.  If these bundles contain entity classes which Hibernate should be able to persist, then the Hibernate configuration should be updated dynamically when the classes are added to or removed from the system.

Hibernate itself does not allow classes to be added to or removed from a SessionFactory instance (it needs to know about all entity classes when it is instantiated).  Consequently we need to create a new SessionFactory whenever the list of entity classes changes in the Hibernate configuration.

This example demonstrates how OSGi bundles can dynamically add new classes to a Hibernate configuration.  We start with a bundle that provides a Hibernate SessionFactory - other bundles can grab and use the SessionFactory to access a database.  However, if a new bundle is installed and it contains new entity classes that are not yet in the SessionFactory configuration, then the SessionFactory must be recreated to update the configuration with the new classes.

If you always use a [HibernateUtil](http://www.hibernate.org/hib_docs/v3/reference/en/html/tutorial.html#tutorial-firstapp-helpers) helper class to get a fresh Hibernate SessionFactory right before using it (i.e., if you never store a local copy of the SessionFactory), then it's straight forward to ensure that you are always working with an up-to-date SessionFactory.  However, if you use Spring Framework to wire up your dao's (data access objects) then it becomes more complicated to ensure that you get a reference to a most up-to-date SessionFactory.

In our sample, we access the SessionFactory via a proxy (called DynamicSessionFactory) which ensures that a SessionFactory with the most up-to-date configuration is always used.  As such, the dao beans must be configured with a reference the dynamic session factory, instead of the usual Hibernate SessionFactory.

## Module List ##

Here is a list of maven projects and modules to implement the solution:

| **provision** | This project downloads resources into the maven repository.  The project does not generate a new artifact. |
|:--------------|:-----------------------------------------------------------------------------------------------------------|
| **hibernate-classes** | Assembles Hibernate classes into a new OSGi bundle                                                         |
| **jta**       | Assembles javax.transaction into a new OSGi bundle                                                         |
| **hsqldb**    | Assembles the HSQLDB jars a new OSGi bundle                                                                |
| **hibernate-session** | Provides a Hibernate SessionFactory that can be dynamically reconfigured during runtime - classes be added to, or removed from the Hibernate configuration as bundles are started and stopped.  This bundle exports a service, DynamicConfiguration, which other bundles can use to register their contributions to the SessionFactory.   The bundle also exports a DynamicSessionFactory which should be used to create Hibernate sessions with the most up to date Hibernate configuration.  [More...] |
| **model-a**   | This bundle contributes an additional class (called "A1") to the Hibernate configuration.  It also provides a DAO service (called "A1Dao") for this class.  |
| **model-b**   | This bundle contributes an additional class (called "B1") to the Hibernate configuration.  It also provides a DAO service (called "B1Dao") for this class.  |
| **integration-tests** | The only way that we currently know if anything is actually happening!  This bundle tests if a dynamically updated Hibernate Session can be used to create, read, update and delete objects of classes A1 and B1 in the database.  |
| **deployment** | This project assembles all dependent bundles along with Windows and Unix batch files (felix.cmd, equinox.cmd, felix.sh and equinox.sh) and configuration files so you can very easily start all the bundles in Equinox either Felix. [More...] |
| **application** | This bundle launches a Swing UI which allows you to execute HQL (Hibernate Query Language) commands and view the entities that the Hibernate session is currently aware of. |

# Todo #

  * Clean up bundle manifests. (After considerable trial and error to get things working, I've ended up with bundle manifest which are messier than they need to be)
  * Write more documentation (for the "More..." links above, and others).

# Comments #

For faster response to your comments, please post them [on my blog](http://notehive.com/wp/2008/07/23/osgi-hibernate-spring-dm-sample/).  Thanks!