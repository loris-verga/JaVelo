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

    public float[] profileSamples(int edgeId){
        //todo really check this for small error and make it better if it works
        if (hasProfile(edgeId) == false){return new float[]{};}

        float[] profilList = new float[1 + (int)Math.ceil(length(edgeId)/2.0)];
        int profileIdAndType = profileIds.get(edgeId);
        int profilType = Bits.extractUnsigned(profileIdAndType,30,2);

        int firstSampleId = Bits.extractUnsigned(profileIdAndType,0,30);
        float firstSample = Q28_4.asFloat(elevations.get(firstSampleId));
        profilList[0] = firstSample;

        float previousSample;
        float newSample;
        float difference;
        int encodedSample;
        int nbOfCompressedValuesInBits;


        switch (profilType){
            case 1:{
                for (int i = 1; i < profilList.length; i++){
                    newSample = Q28_4.asFloat(elevations.get(firstSampleId + i));
                    profilList[i] = newSample;
                }
                break;
            }
            case 2:{
                previousSample = firstSample;
                nbOfCompressedValuesInBits = Short.SIZE/8;
                int indexOfprofilList = 1;
                int totalElevationIndex= (int) Math.ceil((profilList.length - 1.0)/nbOfCompressedValuesInBits);
                for(int elevationIndex = 1 ; elevationIndex <= totalElevationIndex ; elevationIndex++){
                    encodedSample = elevations.get(firstSampleId + elevationIndex);

                    for (int indexOfBits = nbOfCompressedValuesInBits - 1; indexOfBits >= 0 && indexOfprofilList < profilList.length; indexOfBits--){
                        int startOfBits= indexOfBits * 8;
                        difference = Q28_4.asFloat(Bits.extractSigned(encodedSample, startOfBits, 8 ));
                        newSample = previousSample + difference;
                        profilList[indexOfprofilList] = newSample;
                        previousSample = newSample;
                        indexOfprofilList++;
                    }
                }
                break;
            }
            case 3:{
                previousSample = firstSample;
                nbOfCompressedValuesInBits = Short.SIZE/4;
                int indexOfprofilList = 1;
                int totalElevationIndex= (int) Math.ceil((profilList.length - 1.0)/nbOfCompressedValuesInBits);
                for(int elevationIndex = 1 ; elevationIndex <= totalElevationIndex; elevationIndex++) {
                    encodedSample = elevations.get(firstSampleId + elevationIndex);

                    for (int indexOfBits = nbOfCompressedValuesInBits - 1; indexOfBits >= 0 && indexOfprofilList < profilList.length; indexOfBits--) {
                        int startOfBits = indexOfBits * 4;
                        difference = Q28_4.asFloat(Bits.extractSigned(encodedSample, startOfBits, 4));
                        newSample = previousSample + difference;
                        profilList[indexOfprofilList] = newSample;
                        previousSample = newSample;
                        indexOfprofilList++;
                    }
                }
                break;
            }
        }
        if (isInverted(edgeId)){profilList = invertList(profilList);}
        return profilList;
    }

    private float[] invertList(float[] profilSample){
        float[] newprofilSample = new float[profilSample.length];
        for(int i = 0; i < profilSample.length; i++){
            newprofilSample[i] = profilSample[profilSample.length - (i+1)];
        }
        return newprofilSample;
    }

    public int attributesIndex(int edgeId){
        return edgesBuffer.getShort(EDGE_INTS * edgeId + OFFSET_ID_OF_SET_OF_OSM);}


}
