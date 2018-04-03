package com.example.adminuser.mytest;

/**
 * Created by AdminUser on 3/14/2018.
 */

/**
 * Created by AdminUser on 3/14/2018.
 */

public class Grid {
    private int height,width;
    float[] vertices, texels;
    int[] indices;

    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
        initVertices();
        initTexels();
        initIndices();
        transform();
    }

    private void transform() {

        for(int i = 0; i < vertices.length; i += 3) {
            float x=vertices[i],y=vertices[i + 1];
            double r= Math.sqrt(x*x+y*y),r1= ((Math.exp(r / 0.76) - 1) / 3.8342);
            float ratio= (float) (r1/r);
            vertices[i]     = ratio*x;
            vertices[i + 1] = ratio*y;
            vertices[i + 2] = (float) 0.0;
        }
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTexels() {
        return texels;
    }

    public int[] getIndices() {
        return indices;
    }

    private void initVertices() {

        vertices = new float[height*width*3];
        int i = 0;
        float h = (float)height-1;
        float w = (float) width - 1;
        for(int row = 0; row < height; row++) {
            float roww=2*row/h-1;
            for(int col = 0; col < width; col++) {
                float coll=2*col/w-1;
                //vertices[i++] = coll/(2-row/(height-1));
                vertices[i++] = coll;
                vertices[i++] = roww;
                vertices[i++] = 0.0f;
            }
        }
/*
        double r     = Math.sqrt(0.5*0.5 + 1.75*1.75);
        double theta = Math.atan2(1.75, -0.5);
        double r1,theta1;
        for(int row = 0; row < height; row++) {
            r1 = r + row * r/ (height - 1) ;   //这个地方，r必须放在除号前面，否则算之前并不会自动转double导致真个结果一直为零~！！！！！！！
            for (int col = 0; col < width; col++) {
                theta1=(Math.PI-2*theta)/(width-1)*col+theta;
                vertices[i++] = (float) (r1*Math.cos(theta1));
                vertices[i++] = (float) (r1*Math.sin(theta1)-2.75);
                vertices[i++] = 0.0f;
            }
        }
*/
    }


    private void initTexels() {
        texels = new float[height*width*2];
        int i = 0;
        float h = (float)height-1;
        float w = (float) width - 1;
        for(int row = 0; row < height; row++) {
            float roww=row/h;
            for(int col = 0; col < width; col++) {
                float coll=col/w;
                texels[i++] = coll;
                texels[i++] = roww;
            }
        }
    }

    private void initIndices() {
        // http://stackoverflow.com/questions/5915753/generate-a-plane-with-triangle-strips
        indices = new int[getIndicesCount()];
        int i = 0;

        // Indices for drawing cube faces using triangle strips. Triangle
        // strips can be connected by duplicating indices between the
        // strips. If connecting strips have opposite vertex order, then
        // the last index of the first strip and the first index of the
        // second strip need to be duplicated. If connecting strips have
        // same vertex order, then only the last index of the first strip
        // needs to be duplicated.
        for(int row = 0; row < height - 1; row++) {
            if(row % 2 == 0) { // even rows
                for(int col = 0; col < width; col++) {
                    indices[i++] = col + row * width;
                    indices[i++] = col + (row + 1) * width;
                }
            } else {           // odd rows
                for(int col = width - 1; col > 0; col--) {
                    indices[i++] = col + (row + 1) * width;
                    indices[i++] = (col - 1) + row * width;
                }
            }
        }

    }

    public int getIndicesCount() {
        return (height * width) + (width - 1) * (height - 2);
    }
}