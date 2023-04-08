import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.cosmics.rewards.RewardUser;
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
        final var user = Objects.requireNonNull(manager.get(uuid));
        user.addReward(RewardUser.RewardType.KILLED_PLAYERS);
        assert user.getRewards().size() == 1;
    }

    @Test
    public void giveAllOf1() {
        final RewardUserManager manager = plugin.getManager();
        createPlayer();
        final var user = Objects.requireNonNull(manager.get(uuid));
        user.add(RewardUser.RewardType.KILLED_PLAYERS, 1);
        user.add(RewardUser.RewardType.FISHED_FISH, 1);
        user.add(RewardUser.RewardType.HOSTILES_KILLED, 1);
        user.add(RewardUser.RewardType.EVENT, 1);
        user.add(RewardUser.RewardType.BUILT, 1);
        user.add(RewardUser.RewardType.BOSS, 1);
        user.add(RewardUser.RewardType.MINED, 1);
        assert user.getBoss() == 1;
        assert user.getBuilt() == 1;
        assert user.getFished() == 1;
        assert user.getEvent() == 1;
        assert user.getKilled() == 1;
        assert user.getHostiles() == 1;
        assert user.getMined() == 1;
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
