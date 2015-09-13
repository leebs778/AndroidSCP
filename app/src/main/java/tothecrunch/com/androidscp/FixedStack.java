package tothecrunch.com.androidscp;

/**
 * Created by Leebs on 9/3/15.
 */
public class FixedStack {
    private String[][] stack = new String[3][];
    private int size;

    public FixedStack(){
        this.size = 0;
    }

    public void push(String[] obj) {
        if (size == 3){
            String[] temp = {"null"};
            for (int i = 1; i<3;i++){
                temp = stack[i];
                stack[i-1]= temp;
            }
            stack[2] = obj;
        }else{
            stack[++size] = obj;
        }
    }
    public String[] pop() {
        String[] popped = {"NULL"};
        try {
            popped = stack[size--];
            stack[size + 1] = null;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("bad pop!");
        }
        return popped;
    }

    public int getSize() {
        return size;
    }
    public boolean isEmpty(){
        return (size ==0);
    }

}