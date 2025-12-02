//////RUN BACKEND//////
1. Open IntelliJ and open given TestBackend folder
2. Then run the TestBackendApplication.java file
3. this will start running our backend on port 8080

////CREATE PARSER FILES/////

Run these commands on powershell;

    -java -jar lib/jflex-full-1.9.1.jar -d src src/lexer.flex
    -jar lib/java-cup-11b.jar -destdir src -parser parser -symbols sym src/parser.cup

/////REFRESH PROJECT//////
   - Right-click project → Reload from Disk
   - You should now see Lexer.java, parser.java, sym.java in src

///////Build & Run/////////

**Build Project:**
   - Build → Build Project

/////Create Run Configuration & Run compiler///////
   - Run → Edit Configurations
   - + → Application
   - Name: `TestLang Compiler`
   - Main class: `Main`
   - Program arguments: `examples/example.test output/GeneratedTests.java`
   - Click OK
   - Run → Run 'TestLang Compiler'
  
/////COMPILE GENERATEDTEST.java/////////

javac -cp "lib/*" -d output output/GeneratedTests.java

/////RUN COMPILED GENERTEDTEST JAVA FILE///////

java -jar lib/junit-platform-console-standalone-1.10.0.jar --classpath output --scan-class-path

this will show whether test cases have passed or failed (make sure ti run backend otherwise testcases will fail)