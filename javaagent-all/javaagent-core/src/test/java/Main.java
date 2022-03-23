import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Main {

    private static String displayName;
    private static String agentLocation;


    public static void main(String[] args) {
        displayName = args[0];
        agentLocation = args[1];

        //获取当前系统中所有 运行中的 虚拟机
        System.out.println("running JVM start ");
        VirtualMachine.list()
                .stream().filter(vmd -> vmd.displayName().equals(displayName)).findFirst()
                .ifPresent(Main::extracted);


    }

    private static void extracted(VirtualMachineDescriptor virtualMachineDescriptor) {
        try {
            VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor.id());
            virtualMachine.loadAgent(agentLocation);
            virtualMachine.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}