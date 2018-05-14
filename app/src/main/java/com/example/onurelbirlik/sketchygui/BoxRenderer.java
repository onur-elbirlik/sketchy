package com.example.onurelbirlik.sketchygui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import cn.easyar.Vec2F;
import cn.easyar.Matrix44F;

public class BoxRenderer
{
    public static float scaleFactor = 1.0f;
    public static float shiftX = -1.5f;
    public static float shiftY = -1.5f;

    private Context context;
    private static Bitmap bitmap;
    private static boolean bitmapLoaded = false;
    public static float size0 = 2.0f;
    public static float size1 = 2.0f;
    private int program_box;
    private int pos_coord_box;
    private int pos_trans_box;
    private int pos_tex_box;
    private int pos_proj_box;
    private int vbo_coord_box;
    private int vbo_faces_box;
    private int vbo_tex_box;

    private int textureHandle;
    private int textureUniformHandle;

    private String box_vert="uniform mat4 trans;\n"
            + "attribute vec2 aTexCoordinate;\n"
            + "uniform mat4 proj;\n"
            + "attribute vec4 coord;\n"
            + "varying vec2 vTexCoordinate;\n"
            + "\n"
            + "void main(void)\n"
            + "{\n"
            + "    vTexCoordinate = aTexCoordinate;\n"
            + "    gl_Position = proj*trans*coord;\n"
            + "}\n"
            + "\n"
            ;


    private String box_frag="#ifdef GL_ES\n"
            + "precision highp float;\n"
            + "#endif\n"
            + "uniform sampler2D utexture;\n"
            + "varying vec2 vTexCoordinate;\n"
            + "\n"
            + "void main(void)\n"
            + "{\n"
            + "    gl_FragColor = texture2D(utexture, vTexCoordinate);\n"
            + "}\n"
            + "\n"
            ;

    public BoxRenderer(Context context) {
        this.context = context;
    }

    public static void setBitmap(Bitmap bitmap) {
        if(!TakePictureCamera.imageFromCamera) {
            Matrix matrix = new Matrix();

            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            Matrix horMatrix = new Matrix();
            horMatrix.postScale(1, -1, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), horMatrix, true);
        } else {
            Matrix matrix = new Matrix();

            matrix.postRotate(180);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            Matrix horMatrix = new Matrix();
            horMatrix.postScale(1, -1, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), horMatrix, true);
        }

        BoxRenderer.bitmap = addTransparentBorder(bitmap, 4);
        bitmapLoaded = true;
    }

    private static Bitmap addTransparentBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

    private static int setTexture(Bitmap bitmap) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if(textureHandle[0] != 0) {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    private static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if(textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    private float[] flatten(float[][] a)
    {
        int size = 0;
        for (float[] f : a) {
            size += f.length;
        }
        float[] l = new float[size];
        int offset = 0;
        for (float[] f : a) {
            System.arraycopy(f, 0, l, offset, f.length);
            offset += f.length;
        }
        return l;
    }
    private short[] flatten(short[][] a)
    {
        int size = 0;
        for (short[] b : a) {
            size += b.length;
        }
        short[] l = new short[size];
        int offset = 0;
        for (short[] b : a) {
            System.arraycopy(b, 0, l, offset, b.length);
            offset += b.length;
        }
        return l;
    }

    private int generateOneBuffer()
    {
        int[] buffer = {0};
        GLES20.glGenBuffers(1, buffer, 0);
        return buffer[0];
    }

    public void init()
    {
        program_box = GLES20.glCreateProgram();
        int vertShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertShader, box_vert);
        GLES20.glCompileShader(vertShader);
        int fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragShader, box_frag);
        GLES20.glCompileShader(fragShader);
        GLES20.glAttachShader(program_box, vertShader);
        GLES20.glAttachShader(program_box, fragShader);
        GLES20.glLinkProgram(program_box);
        GLES20.glUseProgram(program_box);
        pos_coord_box = GLES20.glGetAttribLocation(program_box, "coord");
        pos_trans_box = GLES20.glGetUniformLocation(program_box, "trans");
        pos_proj_box = GLES20.glGetUniformLocation(program_box, "proj");
        pos_tex_box = GLES20.glGetAttribLocation(program_box, "aTexCoordinate");

        if(bitmapLoaded) {
            textureHandle = setTexture(bitmap);
        } else {
            textureHandle = loadTexture(context, R.mipmap.ic_launcher);
        }
        textureUniformHandle = GLES20.glGetUniformLocation(program_box, "utexture");

        vbo_tex_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_tex_box);
        final float[] cubeTextureCoordinateData =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                        /*
                        // Right face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Left face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Top face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,

                        // Bottom face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                        */
                };
        FloatBuffer cube_texture_buffer = FloatBuffer.wrap(cubeTextureCoordinateData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_texture_buffer.limit() * 4, cube_texture_buffer, GLES20.GL_DYNAMIC_DRAW);

        vbo_coord_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        float cube_vertices[][] = {
                /* +z */{1.0f / 2, 1.0f / 2, 0}, {1.0f / 2, -1.0f / 2, 0}, {-1.0f / 2, -1.0f / 2, 0}, {-1.0f / 2, 1.0f / 2, 0},
                /* -z */{1.0f / 2, 1.0f / 2, 0}, {1.0f / 2, -1.0f / 2, 0}, {-1.0f / 2, -1.0f / 2, 0}, {-1.0f / 2, 1.0f / 2, 0}
        };
        FloatBuffer cube_vertices_buffer = FloatBuffer.wrap(flatten(cube_vertices));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_buffer.limit() * 4, cube_vertices_buffer, GLES20.GL_DYNAMIC_DRAW);

        vbo_faces_box = generateOneBuffer();
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
        short cube_faces[][] = {
                /* +z */{3, 2, 1, 0}, /* -y */{2, 3, 7, 6}, /* +y */{0, 1, 5, 4},
                /* -x */{3, 0, 4, 7}, /* +x */{1, 2, 6, 5}, /* -z */{4, 5, 6, 7}};
        ShortBuffer cube_faces_buffer = ShortBuffer.wrap(flatten(cube_faces));
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, cube_faces_buffer.limit() * 2, cube_faces_buffer, GLES20.GL_STATIC_DRAW);
    }

    public void render(Matrix44F projectionMatrix, Matrix44F cameraview, Vec2F size)
    {
        size0 = size.data[0] * scaleFactor;
        size1 = size.data[1] * scaleFactor;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        float cube_vertices_2[][] = {
                /* +z */{shiftX + size0 / 2, shiftY + size1 / 2, 0},{shiftX + size0 / 2, shiftY +  -size1 / 2, 0},{shiftX + -size0 / 2, shiftY +  -size1 / 2, 0},{shiftX + -size0 / 2, shiftY +  size1 / 2, 0},
                /* -z */{shiftX + size0 / 2, shiftY + size1 / 2, 0},{shiftX + size0 / 2, shiftY + -size1 / 2, 0},{shiftX + -size0 / 2, shiftY + -size1 / 2, 0},{shiftX + -size0 / 2, shiftY + size1 / 2, 0}};
        FloatBuffer cube_vertices_2_buffer = FloatBuffer.wrap(flatten(cube_vertices_2));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_2_buffer.limit() * 4, cube_vertices_2_buffer, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUseProgram(program_box);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_coord_box);
        GLES20.glEnableVertexAttribArray(pos_coord_box);
        GLES20.glVertexAttribPointer(pos_coord_box, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glUniformMatrix4fv(pos_trans_box, 1, false, cameraview.data, 0);
        GLES20.glUniformMatrix4fv(pos_proj_box, 1, false, projectionMatrix.data, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
        for(int i = 0; i < 6; i++) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, 4, GLES20.GL_UNSIGNED_SHORT, i * 4 * 2);
        }
    }
}