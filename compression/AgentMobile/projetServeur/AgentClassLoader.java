public class AgentClassLoader extends ClassLoader {

    public Class<?> chargerClasse(byte[] codeClasse) {
        return defineClass(null, codeClasse, 0, codeClasse.length);
    }
}

