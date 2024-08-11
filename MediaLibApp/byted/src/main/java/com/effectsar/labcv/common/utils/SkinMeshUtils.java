package com.effectsar.labcv.common.utils;

import android.renderscript.Matrix4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SkinMeshUtils {
    public static class BoneInfo {
        public BeVec3f translation;
        public BeQuaternion rotation;
        public BeVec3f scale;

        public BoneInfo(BeVec3f translation, BeQuaternion rotation, BeVec3f scale) {
            this.translation = translation;
            this.rotation = rotation;
            this.scale = scale;
        }

        public BoneInfo deepClone(BoneInfo other) {
            BeVec3f t =  new BeVec3f(other.translation);
            BeVec3f s =  new BeVec3f(other.scale);
            BeQuaternion r =  new BeQuaternion(other.rotation);

            BoneInfo ret = new BoneInfo(t, r, s);
            return  ret;
        }
    }

    public static HashMap<String, BoneInfo>
    readBoneInitPose(String file) {
        HashMap<String, BoneInfo> ret = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            ret = new HashMap<String, BoneInfo>();

            while ((line = br.readLine()) != null) {
                // process the line.
                String[] lines = line.split(" ");
                assert (lines.length == 1 + 3 + 3 + 4);
                String bone = lines[0];
                BeVec3f translation = new BeVec3f();
                BeVec3f scale = new BeVec3f();
                BeQuaternion quaternion = null;

                {
                    float x = Float.parseFloat(lines[1]);
                    float y = Float.parseFloat(lines[2]);
                    float z = Float.parseFloat(lines[3]);

                    translation = new BeVec3f(x, y, z);
                }
                {
                    float x = Float.parseFloat(lines[4]);
                    float y = Float.parseFloat(lines[5]);
                    float z = Float.parseFloat(lines[6]);
                    scale = new BeVec3f(x, y, z);
                }
                {
                    float x = Float.parseFloat(lines[7]);
                    float y = Float.parseFloat(lines[8]);
                    float z = Float.parseFloat(lines[9]);
                    float w = Float.parseFloat(lines[10]);

                    quaternion = new BeQuaternion(x, y, z, w);
                }
                BoneInfo boneInfo = new BoneInfo(translation, quaternion, scale);
                ret.put(bone, boneInfo);
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    public static HashMap<String, String>
    readChildToParentDict(String file){
        HashMap<String, String> ret = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            ret = new HashMap<String, String>();

            while ((line = br.readLine()) != null) {
                // process the line.
                String[] lines = line.split(" ");
                assert (lines.length == 2);
                ret.put(lines[0], lines[1]);
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }


}
