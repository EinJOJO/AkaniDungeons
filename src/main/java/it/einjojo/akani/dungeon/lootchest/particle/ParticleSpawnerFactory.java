package it.einjojo.akani.dungeon.lootchest.particle;

public class ParticleSpawnerFactory {

    public ParticleSpawner createParticleSpawner(String name) {
        return switch (name) {
            case NormalParticleSpawner.NAME -> createNormalParticleSpawner();
            case EpicParticleSpawner.NAME -> createEpicParticleSpawner();
            case LegendaryParticleSpawner.NAME -> createLegendaryParticleSpawner();
            case RareParticleSpawner.NAME -> createRareParticleSpawner();
            case VeryRareParticleSpawner.NAME -> createVeryRareParticleSpawner();
            default -> throw new IllegalArgumentException("Unknown particle spawner: " + name);
        };
    }

    public ParticleSpawner createNormalParticleSpawner() {
        return new NormalParticleSpawner();
    }

    public ParticleSpawner createEpicParticleSpawner() {
        return new EpicParticleSpawner();
    }

    public ParticleSpawner createLegendaryParticleSpawner() {
        return new LegendaryParticleSpawner();
    }

    public ParticleSpawner createRareParticleSpawner() {
        return new RareParticleSpawner();
    }

    public ParticleSpawner createVeryRareParticleSpawner() {
        return new VeryRareParticleSpawner();
    }
}
