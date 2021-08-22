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
public class TrafficData
{
    public String Time,Location,AvgVehicle,AvgVelocity, TotalVehicle, Lane,Station;
    
    public TrafficData()
    {
        
    }
    
    public TrafficData(String RecordedStation,String RecordedTime, String RecordedLocation,String RecordedLane,String RecordedTotalVehicle,String RecordedAvgVehicle, String RecordedAvgVelocity )
    {
       
        Time=RecordedTime;
        Location = RecordedLocation;
        AvgVehicle = RecordedAvgVehicle;
        AvgVelocity = RecordedAvgVelocity;
        TotalVehicle = RecordedTotalVehicle;
        Lane = RecordedLane;
        Station =RecordedStation;
        
    }
}
