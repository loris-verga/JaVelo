package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_ID_OF_DESTINATION_NODE = 0;
    private static final int OFFSET_LENGTH_OF_EDGE = OFFSET_ID_OF_DESTINATION_NODE + Integer.BYTES;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH_OF_EDGE + Short.BYTES;
    private static final int OFFSET_ID_OF_SET_OF_OSM = OFFSET_ELEVATION_GAIN + Short.BYTES;
    private static final int EDGE_INTS = OFFSET_ID_OF_SET_OF_OSM + Short.BYTES;

    //todo not sure if all of these methode should be static..??

    public boolean isInverted(int edgeId){
        if (edgesBuffer.getInt(EDGE_INTS * edgeId + OFFSET_ID_OF_DESTINATION_NODE) < 0){return true;}
        else{return false;}
    }

    public int targetNodeId(int edgeId){
        int destinationNodeId = edgesBuffer.getInt(EDGE_INTS * edgeId + OFFSET_ID_OF_DESTINATION_NODE);
        if (isInverted(edgeId) == false) {return destinationNodeId;}
        else{return ~destinationNodeId;}
    }

    public double length(int edgeId){
        //todo not sure if Q28_4 works here, but gotta try...
        return Q28_4.asDouble(edgesBuffer.getShort(EDGE_INTS * edgeId + OFFSET_LENGTH_OF_EDGE));
    }

    public double elevationGain(int edgeId){
        return  Q28_4.asDouble(edgesBuffer.getShort(EDGE_INTS * edgeId + OFFSET_ELEVATION_GAIN));
    }

    public boolean hasProfile(int edgeId){
        int profileIdAndType = profileIds.get(edgeId);
        int profilType = Bits.extractUnsigned(profileIdAndType,30,2);
        if(profilType != 0){return true;}
        else{return false;}
    }

    float[] profilSamples(int edgeId){
        //check si l'arrete a un profile
        if (hasProfile(edgeId) == false){return new float[]{};}

        float[] profilList = new float[1 + (int)Math.ceil(length(edgeId)/2.0)];
        int profileIdAndType = profileIds.get(edgeId);
        int profilType = Bits.extractUnsigned(profileIdAndType,30,2);

        int firstSampleId = Bits.extractUnsigned(profileIdAndType,0,30);
        float firstSample = Q28_4.asFloat(elevations.get(firstSampleId));
        profilList[0] = firstSample;

        float previousSample;
        float newSample;

        switch (profilType){
            //cas ou les données ne sont pas compresser
            case 1:{
                for (int i = 1; i < profilList.length; i++){
                    // cas ou le sens est inverser, les valeurs stocker dans le buffer sont inverser aussi
                    if (isInverted(edgeId)){newSample = Q28_4.asFloat(elevations.get(firstSampleId - i));}
                    // cas ou le sens est normal on trouve les valeurs normalement
                    else{newSample = Q28_4.asFloat(elevations.get(firstSampleId + i));};
                    profilList[i] = newSample;
                }
                break;
            }
            case 2:{
                previousSample = firstSample;
                for (int i = 1; i < profilList.length; i++) {
                }
                break;
            }
            case 3:{

                break;
            }
        }
        return profilList;
    }
}
