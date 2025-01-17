package editor.list;
import java.util.ArrayList;
import java.util.List;

public class ListStatement {
        private List<Object> objectList;

        public ListStatement() {
            this.objectList = new ArrayList<>();
        }

        public ListStatement(List<Object> objectList) {
            this.objectList = objectList;
        }
        public void add(Object element) {
            objectList.add(element);
        }

        public Object get(int index) {
            if (index < 0 || index >= objectList.size()) {
                throw new IndexOutOfBoundsException("Índice fora dos limites: " + index);
            }
            return objectList.get(index);
        }

        public void clear(){
            objectList.clear();
        }

        public boolean remove(Object element) {
            if (element instanceof Integer && ((Integer) element >= 0 && (Integer) element < objectList.size())) {
                objectList.remove((int) element);
                return true;
            }
            return objectList.remove(element);
        }

        public int size() {
            return objectList.size();
        }

        public void setObjectList(List<Object> objectList) {
            this.objectList = objectList;
        }

        public List<Object> getObjectList() {
            return objectList;
        }

        @Override
        public String toString() {
            return objectList.toString();
        }
    }

