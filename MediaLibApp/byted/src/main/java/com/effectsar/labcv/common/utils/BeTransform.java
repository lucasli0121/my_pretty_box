package com.effectsar.labcv.common.utils;

import com.effectsar.labcv.core.util.LogUtils;

import java.lang.ref.WeakReference;

public class BeTransform {
    public BeVec3f mLocalPos;
    public BeVec3f mLocalScale;
    public BeQuaternion mLocalRotation;
    public BeMatrix4f mLocalMat;
    public BeMatrix4f mWorldMat;
    private String name;

    public boolean dirtyWorldMatrix = true;
    public boolean dirtyUseLocalMatrix = true;
    public boolean dirtyLocalTransForm = true;
    public BeTransform mParent;

    public BeTransform(BeVec3f localPos, BeVec3f localScale, BeQuaternion localRotation) {
        this.mLocalPos = localPos;
        this.mLocalScale = localScale;
        this.mLocalRotation = localRotation;
        mWorldMat = new BeMatrix4f();
        mLocalMat = new BeMatrix4f();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(BeTransform parent) {
        this.mParent = parent;
    }

    public BeMatrix4f getLocalMatrix(){
        if (dirtyLocalTransForm) {
            mLocalMat.setTRS(mLocalPos, mLocalRotation, mLocalScale);
            dirtyLocalTransForm = false;
            dirtyWorldMatrix = true;
        }

        return mLocalMat;
    }

    public BeMatrix4f getWorldMatrix() {
        if (dirtyWorldMatrix) {
            if (mParent == null) {
                mWorldMat.CopyFrom(getLocalMatrix());
            } else {
                mWorldMat = BeMatrix4f.loadMultiply(mParent.getWorldMatrix(), getLocalMatrix());
            }
//            dirtyWorldMatrix = false;
        }
        return mWorldMat;
    }

    public BeVec3f getLocalPosition(){
        if (dirtyUseLocalMatrix) {
            mLocalMat.decompose(mLocalPos, mLocalScale, mLocalRotation);
            dirtyUseLocalMatrix = false;
        }
        return mLocalPos;
    }

    public BeVec3f getWorldPosition() {
        if (mParent != null) {
            BeMatrix4f wolrdMat = this.getWorldMatrix();
            BeVec3f position = new BeVec3f();

            wolrdMat.decompose(position, null, null, null);
            return position;
        }
        return getLocalPosition();
    }

    public void setLocalPosition(BeVec3f position){
        if (dirtyUseLocalMatrix) {
            mLocalMat.decompose(null, mLocalScale, mLocalRotation);
            dirtyUseLocalMatrix = false;
        }
        mLocalPos = new BeVec3f(position);
        dirtyWorldMatrix = true;
        dirtyLocalTransForm = true;
    }

    public void setWorldPosition(BeVec3f newPositon) {
        if (mParent != null) {
            BeVec3f scale =new BeVec3f() , skew = new BeVec3f();
            BeQuaternion quaternion = new BeQuaternion();
            BeMatrix4f localMatrix = new BeMatrix4f(getWorldMatrix());

            localMatrix.decompose(null, scale, quaternion, skew);
            localMatrix.setTRSS(newPositon, quaternion, scale, skew);

            BeMatrix4f parentWorld = mParent.getWorldMatrix();
            BeMatrix4f parentWorldInv = new BeMatrix4f();
            BeMatrix4f.invertFull(parentWorld, parentWorldInv);
            localMatrix = BeMatrix4f.loadMultiply(parentWorldInv, localMatrix);

            BeVec3f position = new BeVec3f();
            localMatrix.decompose(position, null, null, null);
            setLocalPosition(position);
        } else {
            setLocalPosition(newPositon);
        }
    }

    public BeQuaternion getWorldOrientation() {
        if (mParent != null) {
            BeMatrix4f wolrdMat = this.getWorldMatrix();
            BeQuaternion quaternion = new BeQuaternion();

            wolrdMat.decompose(null, null, quaternion, null);
            return quaternion;
        }
        return getLocalOrientation();
    }

    public BeQuaternion getLocalOrientation() {
        if (dirtyUseLocalMatrix) {
            mLocalMat.decompose(mLocalPos, mLocalScale, mLocalRotation);
            dirtyUseLocalMatrix = false;
        }
        return mLocalRotation;
    }

    public void setWorldOrientation(BeQuaternion q) {
        if (mParent != null) {
            BeVec3f pos = new BeVec3f(), scale =new BeVec3f() , skew = new BeVec3f();
            BeQuaternion quaternion = new BeQuaternion();

            BeMatrix4f localMatrix = new BeMatrix4f(getWorldMatrix());
            localMatrix.decompose(pos, scale, null, skew);
            localMatrix.setTRSS(pos, q, scale, skew);

            BeMatrix4f parentWorld = mParent.getWorldMatrix();
            BeMatrix4f parentWorldInv = new BeMatrix4f();
            BeMatrix4f.invertFull(parentWorld, parentWorldInv);
            localMatrix = BeMatrix4f.loadMultiply(parentWorldInv, localMatrix);

            localMatrix.decompose(null, null, quaternion, null);
            setLocalOrientation(quaternion);
        } else {
            setLocalOrientation(q);
        }
    }

    public  void setLocalOrientation(BeQuaternion q) {
        if (dirtyUseLocalMatrix) {
            mLocalMat.decompose(mLocalPos, mLocalScale, null);
            dirtyUseLocalMatrix = false;
        }
        mLocalRotation = new BeQuaternion(q);
        dirtyWorldMatrix = true;
        dirtyLocalTransForm = true;
    }
}