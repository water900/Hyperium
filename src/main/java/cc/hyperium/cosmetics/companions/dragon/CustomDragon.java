package cc.hyperium.cosmetics.companions.dragon;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public class CustomDragon extends EntityTameable {
    /**
     * Ring buffer array for the last 64 Y-positions and yaw rotations. Used to calculate offsets for the animations.
     */
    public double[][] ringBuffer = new double[64][3];
    /**
     * Index into the ring buffer. Incremented once per tick and restarts at 0 once it reaches the end of the buffer.
     */
    public int ringBufferIndex = -1;
    /**
     * Animation time at previous tick.
     */
    public float prevAnimTime;
    /**
     * Animation time, used to control the speed of the animation cycles (wings flapping, jaw opening, etc.)
     */
    public float animTime;

    private double targetX;
    private double targetY;
    private double targetZ;

    public CustomDragon(World worldIn) {
        super(worldIn);

        this.setSize(16.0F, 8.0F);
        setTamed(true);
        this.preventEntitySpawning = false;

        this.tasks.addTask(0, new EntityAIBase() {
            @Override
            public boolean shouldExecute() {
                return CustomDragon.this.getDistanceSqToEntity(getOwner()) > 7*7;
            }

            @Override
            public void updateTask() {
                super.updateTask();

                targetX = getOwner().posX;
                targetY = getOwner().posY;
                targetZ = getOwner().posZ;
            }
        });

        this.tasks.addTask(1, new EntityAIBase() {
            @Override
            public boolean shouldExecute() {
                return true;
            }

            @Override
            public boolean isInterruptible() {
                return true;
            }

            @Override
            public void updateTask() {
                super.updateTask();
                ThreadLocalRandom current = ThreadLocalRandom.current();
                int bound = 4;

                double ownerX = getOwner().posX;
                double ownerY = getOwner().posY;
                double ownerZ = getOwner().posZ;

                targetX = current.nextDouble(-bound + ownerX, bound + ownerX);
                targetY = current.nextDouble(ownerY + 0.5, ownerY + bound);
                targetZ = current.nextDouble(-bound + ownerZ, bound + ownerZ);
            }
        });
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        this.prevAnimTime = this.animTime;

        float f10 = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
        f10 = f10 * (float) Math.pow(2.0D, this.motionY);

        this.animTime += f10;

        if (this.ringBufferIndex < 0) {
            for (int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i][0] = (double) this.rotationYaw;
                this.ringBuffer[i][1] = this.posY;
            }
        }

        if (++this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }

        this.ringBuffer[this.ringBufferIndex][0] = (double) this.rotationYaw;
        this.ringBuffer[this.ringBufferIndex][1] = this.posY;

        double d11 = this.targetX - this.posX;
        double d12 = this.targetY - this.posY;
        double d13 = this.targetZ - this.posZ;

        d12 = d12 / (double) MathHelper.sqrt_double(d11 * d11 + d13 * d13);
        float f17 = 0.6F;
        d12 = MathHelper.clamp_double(d12, (double) (-f17), (double) f17);
        this.motionY += d12 * 0.10000000149011612D;
        this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
        double d4 = 180.0D - MathHelper.atan2(d11, d13) * 180.0D / Math.PI;
        double d6 = MathHelper.wrapAngleTo180_double(d4 - (double) this.rotationYaw);

        if (d6 > 50.0D) {
            d6 = 50.0D;
        }

        if (d6 < -50.0D) {
            d6 = -50.0D;
        }

        Vec3 vec3 = (new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ)).normalize();
        double d15 = (double) (-MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F));
        Vec3 vec31 = (new Vec3((double) MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, d15)).normalize();
        float f5 = ((float) vec31.dotProduct(vec3) + 0.5F) / 1.5F;

        if (f5 < 0.0F) {
            f5 = 0.0F;
        }

        this.randomYawVelocity *= 0.8F;
        float f6 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
        double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;

        if (d9 > 40.0D) {
            d9 = 40.0D;
        }

        this.randomYawVelocity = (float) ((double) this.randomYawVelocity + d6 * (0.699999988079071D / d9 / (double) f6));
        this.rotationYaw += this.randomYawVelocity * 0.1F;
        float f7 = (float) (2.0D / (d9 + 1.0D));
        float f8 = 0.06F;
        this.moveFlying(0.0F, -1.0F, f8 * (f5 * f7 + (1.0F - f7)));

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        Vec3 vec32 = (new Vec3(this.motionX, this.motionY, this.motionZ)).normalize();
        float f9 = ((float) vec32.dotProduct(vec31) + 1.0F) / 2.0F;
        f9 = 0.8F + 0.15F * f9;
        this.motionX *= (double) f9;
        this.motionZ *= (double) f9;
        this.motionY *= 0.9100000262260437D;

        this.renderYawOffset = this.rotationYaw;

        this.updateEntityActionState();
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void moveEntityWithHeading(float strafe, float forward) {
        if (!this.isInWater()) {
            if (!this.isInLava()) {
                float f4 = 0.91F;

                if (this.onGround) {
                    f4 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
                }

                float f = 0.16277136F / (f4 * f4 * f4);
                float f5;

                if (this.onGround) {
                    f5 = this.getAIMoveSpeed() * f;
                } else {
                    f5 = this.jumpMovementFactor;
                }

                this.moveFlying(strafe, forward, f5);
                f4 = 0.91F;

                if (this.onGround) {
                    f4 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
                }

                if (this.isOnLadder()) {
                    float f6 = 0.15F;
                    this.motionX = MathHelper.clamp_double(this.motionX, (double) (-f6), (double) f6);
                    this.motionZ = MathHelper.clamp_double(this.motionZ, (double) (-f6), (double) f6);
                    this.fallDistance = 0.0F;

                    if (this.motionY < -0.15D) {
                        this.motionY = -0.15D;
                    }
                }

                this.moveEntity(this.motionX, this.motionY, this.motionZ);

                if (this.isCollidedHorizontally && this.isOnLadder()) {
                    this.motionY = 0.2D;
                }

                if (this.worldObj.isRemote && (!this.worldObj.isBlockLoaded(new BlockPos((int) this.posX, 0, (int) this.posZ)) || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int) this.posX, 0, (int) this.posZ)).isLoaded())) {
                    if (this.posY > 0.0D) {
                        this.motionY = -0.1D;
                    } else {
                        this.motionY = 0.0D;
                    }
                } else {
                    this.motionY -= 0.08D;
                }

                this.motionY *= 0.9800000190734863D;
                this.motionX *= (double) f4;
                this.motionZ *= (double) f4;
            } else {
                double d1 = this.posY;
                this.moveFlying(strafe, forward, 0.02F);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
                this.motionY -= 0.02D;

                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d1, this.motionZ)) {
                    this.motionY = 0.30000001192092896D;
                }
            }
        } else {
            double d0 = this.posY;
            float f1 = 0.8F;
            float f2 = 0.02F;
            float f3 = (float) EnchantmentHelper.getDepthStriderModifier(this);

            if (f3 > 3.0F) {
                f3 = 3.0F;
            }

            if (!this.onGround) {
                f3 *= 0.5F;
            }

            if (f3 > 0.0F) {
                f1 += (0.54600006F - f1) * f3 / 3.0F;
                f2 += (this.getAIMoveSpeed() * 1.0F - f2) * f3 / 3.0F;
            }

            this.moveFlying(strafe, forward, f2);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double) f1;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= (double) f1;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)) {
                this.motionY = 0.30000001192092896D;
            }
        }

        super.moveEntityWithHeading(strafe, forward);
    }

    /**
     * Returns a double[3] array with movement offsets, used to calculate trailing tail/neck positions. [0] = yaw
     * offset, [1] = y offset, [2] = unused, always 0. Parameters: buffer index offset, partial ticks.
     */
    public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_) {
        if (this.getHealth() <= 0.0F) {
            p_70974_2_ = 0.0F;
        }

        p_70974_2_ = 1.0F - p_70974_2_;
        int i = this.ringBufferIndex - p_70974_1_ * 1 & 63;
        int j = this.ringBufferIndex - p_70974_1_ * 1 - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[i][0];
        double d1 = MathHelper.wrapAngleTo180_double(this.ringBuffer[j][0] - d0);
        adouble[0] = d0 + d1 * (double) p_70974_2_;
        d0 = this.ringBuffer[i][1];
        d1 = this.ringBuffer[j][1] - d0;
        adouble[1] = d0 + d1 * (double) p_70974_2_;
        adouble[2] = this.ringBuffer[i][2] + (this.ringBuffer[j][2] - this.ringBuffer[i][2]) * (double) p_70974_2_;
        return adouble;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
    }
}
