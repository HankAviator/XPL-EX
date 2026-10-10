package eu.faircode.xlua.x.ui.core.view_registry;

import java.util.HashMap;
import java.util.Map;

public class GroupRegistryNotifications implements ISessionObject {

    public static GroupRegistryNotifications create(SharedRegistry stateRegistry) { return new GroupRegistryNotifications(stateRegistry); }

    private final Map<String, IStateChanged> onChangers = new HashMap<>();
    private final SharedRegistry stateRegistry;

    @Override
    public SharedRegistry getRegistry() { return stateRegistry; }

    @Override
    public String getSessionId() { return stateRegistry.getSessionId(); }

    public GroupRegistryNotifications(SharedRegistry stateRegistry) { this.stateRegistry = stateRegistry; }

    public void notifyGroupChange(String groupToNotify, String groupThatChanged) {
        if (groupToNotify == null || groupToNotify.isEmpty()
                || groupThatChanged == null || groupThatChanged.isEmpty()) return;

        // Off-screen/recycled rows have no listener. Their state is already kept
        // in SharedRegistry and is read when they bind again; this is not an error.
        IStateChanged listener = onChangers.get(groupToNotify);
        if (listener != null)
            listener.onGroupChange(ChangedStatesPacket.create(groupThatChanged));
    }

    public void putGroupChangeListener(IStateChanged onChangeListener, String groupName) {
        if (groupName == null || groupName.isEmpty()) return;
        if (onChangeListener == null)
            onChangers.remove(groupName);
        else
            onChangers.put(groupName, onChangeListener);
    }
}
