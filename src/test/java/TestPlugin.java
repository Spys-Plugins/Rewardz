import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.cosmics.rewards.RewardUserManager;
import dev.cosmics.rewards.Rewardz;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.UUID;

public class TestPlugin {
    private static ServerMock server;
    private static Rewardz plugin;
    final UUID uuid = UUID.fromString("e8ba4c2d-effd-4d3c-9fa3-3693924b0f44");

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        plugin = MockBukkit.load(Rewardz.class);
    }

    @Test
    public void createPlayer() {
        server.addPlayer(new PlayerMock(server, "super_cool_spy", uuid));
        assert plugin != null;
        assert plugin.getManager().has(uuid);
    }

    @Test
    public void giveReward() {
        server.addPlayer(new PlayerMock(server, "super_cool_spy", uuid));
        assert plugin != null;
        final RewardUserManager manager = plugin.getManager();
        manager.giveReward(uuid);
        assert Objects.requireNonNull(manager.get(uuid)).getValue8().size() == 1;
    }

    @Test
    public void giveAllOf1() {
        final RewardUserManager manager = plugin.getManager();
        createPlayer();
        manager.addBoss(uuid);
        manager.addBuilt(uuid);
        manager.addFished(uuid);
        manager.addEvent(uuid);
        manager.addKilledMob(uuid);
        manager.addKilledPlayer(uuid);
        manager.addMined(uuid);
        assert Objects.requireNonNull(manager.get(uuid)).getValue1() == 1;
        assert Objects.requireNonNull(manager.get(uuid)).getValue2() == 1;
        assert Objects.requireNonNull(manager.get(uuid)).getValue3() == 1;
        assert Objects.requireNonNull(manager.get(uuid)).getValue4() == 1;
        assert Objects.requireNonNull(manager.get(uuid)).getValue5() == 1;
        assert Objects.requireNonNull(manager.get(uuid)).getValue6() == 1;
        assert Objects.requireNonNull(manager.get(uuid)).getValue7() == 1;
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
