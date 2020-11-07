package worder.commons;

import java.io.*;

public class SerializationUtils<T> {
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        ObjectInputStream ois = new ObjectInputStream(input);
        T obj = (T) ois.readObject();
        ois.close();
        return obj;
    }

    public byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        oos.writeObject(obj);
        output.close();
        return output.toByteArray();
    }
}
