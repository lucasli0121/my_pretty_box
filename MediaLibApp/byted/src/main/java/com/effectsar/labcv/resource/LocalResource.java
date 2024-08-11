package com.effectsar.labcv.resource;


public class LocalResource extends BaseResource {

    private final String path;

    public LocalResource(String name, String path) {
        super(name);
        this.path = path;
    }

    @Override
   void asyncGetResource() {
        resourceListener.onResourceSuccess(this, new ResourceResult(path));
    }

    @Override
    BaseResource.ResourceResult syncGetResource() {
        return new ResourceResult(path);
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("no cancel for local resource");
    }
}
