package sample;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;

public class ExeScript {

   private static final Logger log = Logger.getLogger(ExeScript.class);
   private int iExitValue;
   private String sCommandString;

   public void runScript(String command) throws Exception {
       sCommandString = command;
       CommandLine oCmdLine = CommandLine.parse(sCommandString);
       DefaultExecutor oDefaultExecutor = new DefaultExecutor();
       oDefaultExecutor.setExitValue(0);
       iExitValue = oDefaultExecutor.execute(oCmdLine);

   }
}
