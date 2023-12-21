public record Message(String communicate, String field) {
    Message(String command){
        this(command.split(";")[0], command.split(";")[1]);
    }

    @Override
    public String toString() {
        return communicate + ";" + field;
    }
}
