package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VertexBufferUploader extends WorldVertexBufferUploader
{
    private VertexBuffer vertexBuffer;

    public void draw(BufferBuilder bufferBuilderIn)
    {
        bufferBuilderIn.reset();
        this.vertexBuffer.bufferData(bufferBuilderIn.getByteBuffer());
    }

    public void setVertexBuffer(VertexBuffer vertexBufferIn)
    {
        this.vertexBuffer = vertexBufferIn;
    }
}
