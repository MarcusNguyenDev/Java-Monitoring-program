/*
 * PROGRAM: Monitoring Program
 * Author: Kim Long
 * ID: 470962509
 * VERSION: R.1.0
 * INPUT: Standard input
 * OUTPUT: Standard Output
 * Note: This is for Monitoring Office
 */
package monitoringprogram;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * StringUtil
 * @author student
 */
public class MainForm extends javax.swing.JFrame
{
    // <editor-fold defaultstate="collapsed" desc="Variables Declaration">
    //Network related ---------------------------
    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private MonitoringOfficeThread client = null;
    private String serverName = "localhost";
    private int serverPort = 4444;
    //Array related
    static int numberOfStation = 50;
    static int currentStation = 0;
    static TrafficData wordList[] = new TrafficData[numberOfStation];
    private String[] SortedwordList = new String[numberOfStation];
    
    //Other variable
    BinaryTree tree = new BinaryTree();
    MyModel model;
    ArrayList<Object[]> array = new ArrayList();
    String columnNames[] =
    {
        "Time", "Location", "Avg Vehicle", "Avg Velocity"
    };
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public MainForm()
    {
        readFile();
        initComponents();
        SeedTable();
        PopulateTable();
        getParameters();
        CreateBinaryTree();
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Networking Methods">
    
    //This method is to establish the connection between server and client
    public void connect(String serverName, int serverPort)
    {
        println("Establishing connection. Please wait ...");
        try
        {
            socket = new Socket(serverName, serverPort);
            println("Connected: " + socket);
            open();
        } catch (UnknownHostException uhe)
        {
            println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe)
        {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }
    //This method is to send the message
     private void send(String Data)
    {
        try
        {
            streamOut.writeUTF(Data);
            streamOut.flush();
        } catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }
     //This method define the way the system handle the message
    public void handle(String msg)
    {
        String msgtemp[] = msg.split(" ");
        if (msgtemp[1].equals("checkstatus")||msgtemp[1].equals("good") )
        {
            txtareaNewData.append("Port:"+msgtemp[0]+" Status = Good\n");
        } 
        else
        {
            
            String temp[] = msgtemp[1].split(",");
            wordList[currentStation] = new TrafficData(temp[0],temp[1],temp[2],temp[3],temp[4],temp[5],temp[6]);
            txtareaNewData.append("New data from station: "+temp[0]+" \n"+msgtemp[1]+"\n");
            array.add(new Object[]
            {temp[1],temp[2],temp[5],temp[6]});
            PopulateTable();
            currentStation++;
        }
    }
    //open connection
    public void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new MonitoringOfficeThread(this, socket);
        } catch (IOException ioe)
        {
            println("Error opening output stream: " + ioe);
        }
    }
    //close connection
    public void close()
    {
        try
        {
            if (streamOut != null)
            {
                streamOut.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        } catch (IOException ioe)
        {
            println("Error closing ...");
        }
        client.close();
        client.stop();
    }

    public String GetMsg(String msg)
    {
        return msg;
    }

    void println(String msg)
    {
        lblMessage.setText(msg);
    }

    public void getParameters()
    {
//        serverName = getParameter("host");
//        serverPort = Integer.parseInt(getParameter("port"));

        serverName = "localhost";
        serverPort = 4444;
    }

    public void setMsg(String msg)
    {
        lblMessage.setText(msg);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Read & Write">
    public void readFile() {
        // Try to read in the data and if an exception occurs go to the Catch section 
        try {
            // Set up vaious streams for reading in the content of the data file.
            FileInputStream fstream = new FileInputStream("Traffic.csv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = br.readLine()) != null) {

                String[] temp = line.split(",");
                wordList[currentStation] = new TrafficData(temp[0],temp[1],temp[2],temp[3],temp[4],temp[5],temp[6]);
                currentStation++;
               
            }

            br.close();            // Close the BufferedReader
            in.close();            // Close the DataInputStream
            fstream.close();       // Close the FileInputStream
        } catch (Exception e) {
            // If an exception occurs, print an error message on the console.
            System.err.println("Error Reading File: " + e.getMessage());
        }
    }

    public void writeFile(String file) {
        // Try to print out the data and if an exception occurs go to the Catch section 
        try {
            //       with the parameter variable: fileName 
            // Set up a PrintWriter for printing the array content out to the data file.
            PrintWriter out = new PrintWriter(new FileWriter(file));

            for(int i=0;i<currentStation;i++)
            {
                out.println(wordList[i].Station+","+wordList[i].Time+","+wordList[i].Location+","+wordList[i].Lane+","+wordList[i].TotalVehicle+","+wordList[i].AvgVehicle+","+wordList[i].AvgVelocity);
            }
            
            

            // Close the printFile (and in so doing, empty the print buffer)
            out.close();
        } catch (Exception e) {
            // If an exception occurs, print an error message on the console.
            System.err.println("Error Writing File: " + e.getMessage());
        }
    }
    
    public void writeBNFile(String file) {
        // Try to print out the data and if an exception occurs go to the Catch section 
        try {
            //       with the parameter variable: fileName 
            // Set up a PrintWriter for printing the array content out to the data file.
            PrintWriter out = new PrintWriter(new FileWriter(file));

            for(int i=0;i<currentStation;i++)
            {
                out.println(SortedwordList[i]);
            }
            
            

            // Close the printFile (and in so doing, empty the print buffer)
            out.close();
        } catch (Exception e) {
            // If an exception occurs, print an error message on the console.
            System.err.println("Error Writing File: " + e.getMessage());
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Table">
    
    public void SeedTable()
    {
//        // Create some data
//        array.add(new Object[]
//        {
//            "Time 1", "abc 13", "12", "35"
//        });
//        array.add(new Object[]
//        {
//            "Time 2", "aaa 15", "15", "57"
//        });
//
//        array.add(new Object[]
//        {
//            "Time 1", "bbb 14", "13", "45"
//        });
//        array.add(new Object[]
//        {
//            "Time 2", "bcd 12", "53", "21"
//        });
//        array.add(new Object[]
//        {
//            "Time 3", "gfr 3", "78", "12"
//        });
//        array.add(new Object[]
//        {
//            "Time 4", "wasd 2", "65", "86"
//        });
//        array.add(new Object[]
//        {
//            "Time 3", "gfdd 1", "5", "35"
//        });
//        array.add(new Object[]
//        {
//            "Time 4", "rtyu 4", "8", "37"
//        });
//        array.add(new Object[]
//        {
//            "Time 2", "iyjh 5", "97", "280"
//        });
//        array.add(new Object[]
//        {
//            "Time 3", "uthg 6", "88", "68"
//        });
//        array.add(new Object[]
//        {
//            "Time 4", "qweas 7", "65", "78"
//        });
//        array.add(new Object[]
//        {
//            "Time 1", "ouiyu 11", "9", "12"
//        });
//        array.add(new Object[]
//        {
//            "Time 2", "uythg 10", "3", "59"
//        });
//        array.add(new Object[]
//        {
//            "Time 3", "erte 8", "5", "75"
//        });
//        array.add(new Object[]
//        {
//            "Time 4", "rewe 9", "2", "11"
//        });
//        array.add(new Object[]
//        {
//            "Time 1", "sdfh 16", "650", "35"
//        });
        
          for (int i = 0; i < currentStation; i++)
        {
            array.add(new Object[]
            {
                wordList[i].Time,wordList[i].Location,wordList[i].AvgVehicle,wordList[i].AvgVelocity
            });
        }

    }

    public void PopulateTable()
    {
        model = new MyModel(array, columnNames);
        tbl.setModel(model);
    }

    class MyModel extends AbstractTableModel
    {

        ArrayList<Object[]> al;

        // the headers
        String[] header;

        // constructor 
        MyModel(ArrayList<Object[]> obj, String[] header)
        {
            // save the header
            this.header = header;
            // and the data
            al = obj;
        }

        // method that needs to be overload. The row count is the size of the ArrayList
        public int getRowCount()
        {
            return al.size();
        }

        // method that needs to be overload. The column count is the size of our header
        public int getColumnCount()
        {
            return header.length;
        }

        // method that needs to be overload. The object is in the arrayList at rowIndex
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            return al.get(rowIndex)[columnIndex];
        }

        // a method to return the column name 
        public String getColumnName(int index)
        {
            return header[index];
        }

        // a method to add a new line to the table
        void add(String word1, String word2, String word3, String word4)
        {
            // make it an array[2] as this is the way it is stored in the ArrayList
            // (not best design but we want simplicity)
            String[] str = new String[4];
            str[0] = word1;
            str[1] = word2;
            str[2] = word3;
            str[3] = word4;
            al.add(str);
            // inform the GUI that I have change
            fireTableDataChanged();
        }

    }

    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Sorts">
    public void BubbleSort(ArrayList<Object[]> arr)
    {
        for (int j = 0; j < arr.size(); j++)
        {
            for (int i = j + 1; i < arr.size(); i++)
            {
                if ((arr.get(i)[1]).toString().compareToIgnoreCase(arr.get(j)[1].toString()) > 0)
                {
                    Object[] words = arr.get(j);
                    arr.set(j, arr.get(i));
                    arr.set(i, words);
                }
            }
        }
    }
    
    
    public void InsertionSort(ArrayList<Object[]> arr)
    {
        int j;
        Object[] key;
        int i;

        for (j = 1; j < arr.size(); j++)
        {
            key = arr.get(j);
            for (i = j - 1; (i >= 0) && ((arr.get(i)[2]).toString().compareToIgnoreCase(key[2].toString()) > 0); i--)
            {
                arr.set(i + 1, arr.get(i));
            }
            arr.set(i + 1, key);
        }
    }
    
 public void ExchangeSort(ArrayList<Object[]> arr)
    {
        int i, j;
        Object[] temp;
        for (i = 0; i < arr.size() - 1; i++)
        {
            for (j = i + 1; j < arr.size(); j++)
            {
                if ((arr.get(i)[3]).toString().compareToIgnoreCase(arr.get(j)[3].toString()) > 0)
                {
                    temp = arr.get(i);
                    arr.set(i, arr.get(j));
                    arr.set(j, temp);
                }
            }
        }
    }

    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="LinkedList">
    public void DisplayLinkedList()
    {
        DList dlist = new DList();
        for (int i = 0; i < array.size(); i++)
        {
            dlist.head.insert(new Node("",(array.get(i)[0]).toString(),(array.get(i)[1]).toString(),"","",(array.get(i)[2]).toString(),(array.get(i)[3]).toString()));
        }
        txtareaLinkedList.setText(dlist.toString());

    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Binary Tree">
    public void CreateBinaryTree()
    {
        for (int i = 0; i < array.size(); i++)
        {
            tree.addBTNode(Integer.parseInt(wordList[i].TotalVehicle), (wordList[i].Station+","+wordList[i].Time+","+wordList[i].Location+","+wordList[i].Lane+","+wordList[i].TotalVehicle+","+wordList[i].AvgVehicle+","+wordList[i].AvgVelocity));
        }
    
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Helper Methods">
    public String getArrayData(int Position)
    {
        String str;
        str = (array.get(Position)[0]).toString() + "," + (array.get(Position)[1]).toString() + "," + (array.get(Position)[2]).toString() + "," + (array.get(Position)[3]).toString();
        return str;
    }
   
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jRadioButton1 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtareaNewData = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        btnVelocity = new javax.swing.JButton();
        btnVehicle = new javax.swing.JButton();
        btnLocation = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtareaLinkedList = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtareaBinaryTree = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnCheckStatus = new javax.swing.JButton();
        btnSvPreOrder = new javax.swing.JButton();
        btnSvInOrder = new javax.swing.JButton();
        btnSvPostOrder = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnDisPreOrder = new javax.swing.JButton();
        btnDisInOrder = new javax.swing.JButton();
        btnDisPostOrder = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(255, 255));
        setName("Monitoring Office"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt)
            {
                formWindowOpened(evt);
            }
        });

        tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl.setName(""); // NOI18N
        jScrollPane1.setViewportView(tbl);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel1.setText("                              Monitoring Office");

        txtareaNewData.setColumns(20);
        txtareaNewData.setRows(5);
        jScrollPane2.setViewportView(txtareaNewData);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("New Data received from:");

        btnVelocity.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnVelocity.setText("Velocity");
        btnVelocity.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnVelocityActionPerformed(evt);
            }
        });

        btnVehicle.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnVehicle.setText("Vehicle");
        btnVehicle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnVehicleActionPerformed(evt);
            }
        });

        btnLocation.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnLocation.setText("Location");
        btnLocation.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnLocationActionPerformed(evt);
            }
        });

        txtareaLinkedList.setColumns(20);
        txtareaLinkedList.setRows(5);
        jScrollPane3.setViewportView(txtareaLinkedList);

        txtareaBinaryTree.setColumns(20);
        txtareaBinaryTree.setRows(5);
        jScrollPane4.setViewportView(txtareaBinaryTree);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Linked List");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Binary Tree");

        btnCheckStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCheckStatus.setText("Check Status");
        btnCheckStatus.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnCheckStatusActionPerformed(evt);
            }
        });

        btnSvPreOrder.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSvPreOrder.setText("Save");
        btnSvPreOrder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnSvPreOrderActionPerformed(evt);
            }
        });

        btnSvInOrder.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSvInOrder.setText("Save");
        btnSvInOrder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnSvInOrderActionPerformed(evt);
            }
        });

        btnSvPostOrder.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSvPostOrder.setText("Save");
        btnSvPostOrder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnSvPostOrderActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setText("Pre-Order");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("In-Order");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setText("Post-Order");

        btnDisPreOrder.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnDisPreOrder.setText("Display");
        btnDisPreOrder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnDisPreOrderActionPerformed(evt);
            }
        });

        btnDisInOrder.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnDisInOrder.setText("Display");
        btnDisInOrder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnDisInOrderActionPerformed(evt);
            }
        });

        btnDisPostOrder.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnDisPostOrder.setText("Display");
        btnDisPostOrder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnDisPostOrderActionPerformed(evt);
            }
        });

        btnExit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnExitActionPerformed(evt);
            }
        });

        lblMessage.setText("jLabel8");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(614, 614, 614)
                        .addComponent(btnLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btnVehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(btnVelocity, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMessage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSvPreOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnDisPreOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnSvInOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(329, 329, 329)
                                        .addComponent(btnSvPostOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnDisInOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnDisPostOrder, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(btnExit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane4)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnCheckStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 925, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVelocity)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLocation)
                        .addComponent(btnVehicle)
                        .addComponent(btnCheckStatus)))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDisPreOrder)
                    .addComponent(btnDisInOrder)
                    .addComponent(btnDisPostOrder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSvPreOrder)
                    .addComponent(btnSvInOrder)
                    .addComponent(btnSvPostOrder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMessage)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Events">
    private void btnVelocityActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnVelocityActionPerformed
    {//GEN-HEADEREND:event_btnVelocityActionPerformed
        ExchangeSort(array);
        PopulateTable();
        DisplayLinkedList();
    }//GEN-LAST:event_btnVelocityActionPerformed

    private void btnVehicleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnVehicleActionPerformed
    {//GEN-HEADEREND:event_btnVehicleActionPerformed
        InsertionSort(array);
        PopulateTable();
        DisplayLinkedList();
    }//GEN-LAST:event_btnVehicleActionPerformed

    private void btnLocationActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnLocationActionPerformed
    {//GEN-HEADEREND:event_btnLocationActionPerformed
        BubbleSort(array);
        PopulateTable();
        DisplayLinkedList();
    }//GEN-LAST:event_btnLocationActionPerformed

    private void btnCheckStatusActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCheckStatusActionPerformed
    {//GEN-HEADEREND:event_btnCheckStatusActionPerformed
        send("checkstatus");
    }//GEN-LAST:event_btnCheckStatusActionPerformed

    private void btnSvPreOrderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSvPreOrderActionPerformed
    {//GEN-HEADEREND:event_btnSvPreOrderActionPerformed
        txtareaBinaryTree.setText("");
        tree.preorderTraverseTree(tree.root,txtareaBinaryTree);
        String data = txtareaBinaryTree.getText();
        String temp[] = data.split(" ");
        for (int i = 0; i < temp.length; i++)
        {
            SortedwordList[i] = (tree.findBTNode(Integer.parseInt(temp[i]))).toString();
        }
        writeBNFile("PreOrder.csv");
        
        
    }//GEN-LAST:event_btnSvPreOrderActionPerformed

    private void btnSvInOrderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSvInOrderActionPerformed
    {//GEN-HEADEREND:event_btnSvInOrderActionPerformed
        txtareaBinaryTree.setText("");
        tree.inOrderTraverseTree(tree.root,txtareaBinaryTree);
        String data = txtareaBinaryTree.getText();
        String temp[] = data.split(" ");
        for (int i = 0; i < temp.length; i++)
        {
             SortedwordList[i] = (tree.findBTNode(Integer.parseInt(temp[i]))).toString();
        }
        writeBNFile("InOrder.csv");
    }//GEN-LAST:event_btnSvInOrderActionPerformed

    private void btnSvPostOrderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSvPostOrderActionPerformed
    {//GEN-HEADEREND:event_btnSvPostOrderActionPerformed
        txtareaBinaryTree.setText(""); 
        tree.postOrderTraverseTree(tree.root,txtareaBinaryTree);
        String data = txtareaBinaryTree.getText();
        String temp[] = data.split(" ");
        for (int i = 0; i < temp.length; i++)
        {
            SortedwordList[i] = (tree.findBTNode(Integer.parseInt(temp[i]))).toString();
        }
        writeBNFile("PostOrder.csv");
    }//GEN-LAST:event_btnSvPostOrderActionPerformed

    private void btnDisPreOrderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDisPreOrderActionPerformed
    {//GEN-HEADEREND:event_btnDisPreOrderActionPerformed
        txtareaBinaryTree.setText("");
        tree.preorderTraverseTree(tree.root,txtareaBinaryTree);
    }//GEN-LAST:event_btnDisPreOrderActionPerformed

    private void btnDisInOrderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDisInOrderActionPerformed
    {//GEN-HEADEREND:event_btnDisInOrderActionPerformed
        txtareaBinaryTree.setText("");
        tree.inOrderTraverseTree(tree.root,txtareaBinaryTree);
    }//GEN-LAST:event_btnDisInOrderActionPerformed

    private void btnDisPostOrderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDisPostOrderActionPerformed
    {//GEN-HEADEREND:event_btnDisPostOrderActionPerformed
        txtareaBinaryTree.setText(""); 
        tree.postOrderTraverseTree(tree.root,txtareaBinaryTree);
    }//GEN-LAST:event_btnDisPostOrderActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnExitActionPerformed
    {//GEN-HEADEREND:event_btnExitActionPerformed
        writeFile("Traffic.csv");
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowOpened
    {//GEN-HEADEREND:event_formWindowOpened
        connect(serverName,serverPort);        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        writeFile("Traffic.csv");
    }//GEN-LAST:event_formWindowClosing
    //</editor-fold>
   
    //<editor-fold defaultstate="collapsed" desc="Main Method">
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new MainForm().setVisible(true);
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generated Variable">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckStatus;
    private javax.swing.JButton btnDisInOrder;
    private javax.swing.JButton btnDisPostOrder;
    private javax.swing.JButton btnDisPreOrder;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnLocation;
    private javax.swing.JButton btnSvInOrder;
    private javax.swing.JButton btnSvPostOrder;
    private javax.swing.JButton btnSvPreOrder;
    private javax.swing.JButton btnVehicle;
    private javax.swing.JButton btnVelocity;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JTable tbl;
    private javax.swing.JTextArea txtareaBinaryTree;
    private javax.swing.JTextArea txtareaLinkedList;
    private javax.swing.JTextArea txtareaNewData;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
