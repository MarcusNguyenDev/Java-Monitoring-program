/**
 * **************************************************************************
 */
/*                                                                           */
/*                    Doubly-Linked List Manipulation                        */
/*                                                                           */
/*                     January 1998, Toshimi Minoura                         */
/*                                                                           */
/**
 * **************************************************************************
 */
// Filename: Doubly-LinkedList_ToshimiMinoura
// Source:   TBA
package monitoringprogram;

// A Node is a node in a doubly-linked list.

class Node
{              // class for nodes in a doubly-linked list

    Node prev;              // previous Node in a doubly-linked list
    Node next;              // next Node in a doubly-linked list
    TrafficData data;
    //public char data;       // data stored in this Node

    Node()
    {                // constructor for head Node 
        prev = this;           // of an empty doubly-linked list
        next = this;
        data = new TrafficData();
//    data.Word1 = "Yellow";
//    data.Word2 = "No";
        // data = 'H';           // not used except for printing data in list head
    }

    Node(String RecordedTime, String RecordedLocation,String RecordedAvgVehicle, String RecordedAvgVelocity, String RecordedLane, String RecordedTotalVehicle, String RecordedStation)
    {       // constructor for a Node with data
        prev = null;
        next = null;
        data = new TrafficData(RecordedTime,RecordedLocation,RecordedAvgVehicle,RecordedAvgVelocity,RecordedLane,RecordedTotalVehicle,RecordedStation);
        //this.data = data;     // set argument data to instance variable data
    }

    public void append(Node newNode)
    {  // attach newNode after this Node
        newNode.prev = this;
        newNode.next = next;
        if (next != null)
        {
            next.prev = newNode;
        }
        next = newNode;
////        System.out.println("Node with data " + newNode.data.Station
////                + " appended after Node with data " + data.Station);
    }

    public void insert(Node newNode)
    {  // attach newNode before this Node
        newNode.prev = prev;
        newNode.next = this;
        prev.next = newNode;;
        prev = newNode;
////        System.out.println("Node with data " + newNode.data.Station
////                + " inserted before Node with data " + data.Station);
    }

    public void remove()
    {              // remove this Node
        next.prev = prev;                 // bypass this Node
        prev.next = next;
        System.out.println("Node with data " + data.Location + " removed");
    }
    public String toString(){
        return  this.data.Time + " - " + this.data.Location + " - " + this.data.AvgVehicle + " - " + this.data.AvgVelocity;
    }
}

class DList
{

    Node head;
    
    public DList()
    {
        head = new Node();
    }

    public DList(String RecordedStation,String RecordedTime, String RecordedLocation,String RecordedLane,String RecordedTotalVehicle,String RecordedAvgVehicle, String RecordedAvgVelocity )
    {
        head = new Node(RecordedStation,RecordedTime,RecordedLocation,RecordedLane,RecordedTotalVehicle,RecordedAvgVehicle,RecordedAvgVelocity);
    }

    public Node find(String station)
    {          // find Node containing x
        for (Node current = head.next; current != head; current = current.next)
        {
            if (current.data.Location.compareToIgnoreCase(station) == 0)
            {        // is x contained in current Node?
                System.out.println("Sation " + station + " Data found");
                return current;               // return Node containing x
            }
        }
        System.out.println("Data " + station + " Data not found");
        return null;
    }

    //This Get method Added by Matt C
    public Node get(int i)
    {
        Node current = this.head;
        if (i < 0 || current == null)
        {
            throw new ArrayIndexOutOfBoundsException();
        }
        while (i > 0)
        {
            i--;
            current = current.next;
            if (current == null)
            {
                throw new ArrayIndexOutOfBoundsException();
            }
        }
        return current;
    }

    public String toString()
    {
        String str = "";
        if (head.next == head)
        {             // list is empty, only header Node
            return "List Empty";
        }
        str = "HEAD = ";
        for (Node current = head.next; current != head && current != null; current = current.next)
        {
            str = str + current.data.Time+"-"+current.data.Location+"-"+current.data.AvgVelocity+"-"+current.data.AvgVehicle+"-" + " <-> ";
        }
        str=str+"TAIL";
        return str;
    }

    public void print()
    {                  // print content of list
        if (head.next == head)
        {             // list is empty, only header Node
            System.out.println("list empty");
            return;
        }
        System.out.print("list content = ");
        for (Node current = head.next; current != head; current = current.next)
        {
            System.out.print(" " + current.data.Location);
        }
        System.out.println("");
    }

//  public static void main(String[] args) {
//    DList dList = new DList();              // create an empty dList
//    dList.print();
//
//    dList.head.append(new Node("1","2"));       // add Node with data '1'
//    dList.print();
//    dList.head.append(new Node("3", "4"));       // add Node with data '2'
//    dList.print();
//    dList.head.append(new Node("5","6"));       // add Node with data '3'
//    dList.print();
//    dList.head.insert(new Node("A","B"));       // add Node with data 'A'
//    dList.print();
//    dList.head.insert(new Node("C","D"));       // add Node with data 'B'
//    dList.print();
//    dList.head.insert(new Node("E","F"));       // add Node with data 'C'
//    dList.print();
//
//    Node nodeA = dList.find("A");           // find Node containing 'A'
//    nodeA.remove();                         // remove that Node
//    dList.print();
//
//    Node node2 = dList.find("3");           // find Node containing '2'
//    node2.remove();                           // remove that Node
//    dList.print();
//
//    Node nodeB = dList.find("5");            // find Node containing 'B'
//    nodeB.append(new Node("Linked","List"));   // add Node with data X
//    dList.print();
//  }
}
