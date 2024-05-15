package it.einjojo.akani.dungeon.lootchest.particle;

public class ParticleSpawnerFactory {

    public ParticleSpawner createParticleSpawner(String name) {
        return switch (name) {
            case "normal" -> new NormalParticleSpawner();
            case "epic" -> new EpicParticleSpawner();
            default -> throw new IllegalArgumentException("Unknown particle spawner: " + name);
        };
    }

}
