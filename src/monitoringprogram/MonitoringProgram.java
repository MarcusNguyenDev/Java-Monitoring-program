/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoringprogram;

/**
 *
 * @author student
 */
public class MonitoringProgram
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        MainForm fr = new MainForm();
        fr.setVisible(true);
        
        MonitoringServer server = new MonitoringServer(4444);
        if (args.length != 1)
        {
            //System.out.println("Usage: java ChatServer port");
            server = new MonitoringServer(4444);
        }
        else
        {
            server = new MonitoringServer(Integer.parseInt(args[0]));
        }
    }
    
}
