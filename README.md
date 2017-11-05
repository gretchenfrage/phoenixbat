# PhoenixBat

### What's PhoenixBat?
It's like coding codingbat, except the instructor can create their own problems.

### How to use?
The program is an executable jar file, which runs in a gui. That file, and all other files, are available in the releases section of this repository. When you open it, it prompts you for two locations on the file system. The problem suite is a .jar file created by the instructor that contains all the problems, and all the test cases for all the problems. The working directory is the directory that contains the student's .class files.

If the student is using BlueJ, they can set the working directory to the BlueJ project's directory.

![enter window](https://imgur.com/download/LcDvT02)

This will enter the PhoenixBat testing window, in which the Test button can be pressed to test the code. Initially, all problems will fail, because they have not been written yet. 

The instructor may wish to provide the students with skeleton code, in which the methods to be tested simply throw an Error. This would allow these methods to be commented, describing what the must do. This would also help avoid difficulties in setting up the project.

Each problem represents a single method that is expected to behave in a certain way, with some test cases that confirm or deny that the student's code works. Some of these test cases can be visible to the student, and other can be opaque.

The way this is applied, is that the student writes and compiles a class for each problem, with a single static method, called 'apply'. This method can have any arguments and any return type. Once the student implements this method, they can re-run the tests to receive feedback.

![implementation](https://imgur.com/download/agqG2aG)
![main window](https://imgur.com/download/0LbNP2f)

These images show one problem being correctly implemented, and PhoenixBat confirming that the problem works. With the problem expanded, it is visible that there are two visible test cases and one opaque test case. The rest of the problems still fail, because they have not been implemented.

Test cases have five possible results, all of which will be displayed: the test passed, the test gave the wrong output, the test threw an exception, the class file doesn't exist, or the class file was corrupted.