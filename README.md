# PhoenixBat

### What's PhoenixBat?
It's like coding codingbat, except the instructor can create their own problems.

### How to use
The program is an executable jar file, which runs in a GUI. That file, and all other files, are available in the releases section of this repository. When you open it, it prompts you for two locations on the file system. The problem suite is a .jar file created by the instructor that contains all the problems, and all the test cases for all the problems. The working directory is the directory that contains the student's .class files.

If the student is using BlueJ, they can set the working directory to the BlueJ project's directory.

![enter window](https://imgur.com/download/LcDvT02)

Press the enter button. This will enter the PhoenixBat testing window, in which the Test button can be pressed to test the code. Initially, all problems will fail, because they have not been written yet. 

The instructor may wish to provide the students with skeleton code, in which the methods to be tested simply throw an Error. This would allow these methods to be commented, describing what the must do. This would also help avoid difficulties in setting up the project.

Each problem represents a single method that is expected to behave in a certain way, with some test cases that confirm or deny that the student's code works. Some of these test cases can be visible to the student, and other can be opaque.

The way this works is that the student writes and compiles a class for each problem, with a single static method, called 'apply'. This method can have any arguments and any return type. Once the student implements this method, they can re-run the tests to receive feedback.

![implementation](https://imgur.com/download/agqG2aG)
![main window](https://imgur.com/download/0LbNP2f)

These images show one problem being correctly implemented, and PhoenixBat confirming that the problem works. With the problem expanded, it is visible that there are two visible test cases and one opaque test case. The rest of the problems still fail, because they have not been implemented.

Test cases have five possible results, all of which will be displayed: the test passed, the test gave the wrong output, the test threw an exception, the class file doesn't exist, or the class file was corrupted.

### How to create problem suites
There is a jar file containing the PhoenixBat API which is used to create test suites. Download this jar, and create a Java project in your favorite IDE. Configure your project to use the API jar as a library, and prepare to export your own code as a jar file. **The jar file that you create must not contain the PhoenixBat API, make sure to configure it accordingly**.

For each problem, create a class representing that problem, and annotation that problem using @Problem. The @Problem annotation has a required parameter, name, which must be the name of the class that the students will implement. The problem suite class that represents the problem must not have the same name as the class the students create for the problem. The @Problem annotation also has an ordinal parameter, which is the index of that problem in the list of problems, as they are displayed in PhoenixBat.

There are two types of tests that you can add to these problems, equality tests, and acceptance tests.

Equality tests represent an array of method input, and an expected method output. If the student's method output, given those inputs, .equals the expected output, the test passes. To create a test within a problem, create a method (does not need to be static), with whatever name you want, that returns an EqualityBean. The EqualityBean is a simple data-container class, which you construct with, first, the expected output, and then, all the input parameters. Then, annotate that method with @EqualityTest. The @EqualityTest annotation accepts an optional parameter, which is the ordinal of the test within the problem.

If you wish for the test to be hidden from the student, also annotate it with @Hidden.

To put all that into action, here is the code for the previously exhibited Uppercase test:

    @Problem(name = "Uppercase", ordinal = 1)
    public class UppercaseTest {
       @EqualityTest(ordinal = 2)
       public EqualityBean helloworld() {
           return new EqualityBean("HELLO WORLD", "hello world");
       }
   
       @EqualityTest(ordinal = 1)
       public EqualityBean goodbyteworld() {
           return new EqualityBean("GOODBYTE WORLD", "goOdByTe wOrLd");
       }
   
       @EqualityTest(ordinal = 3)
       @Hidden
       public EqualityBean foobar() {
           return new EqualityBean("FOO BAR", "FOO BAR");
       }
    }
