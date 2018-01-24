package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> sectionList = Lists.<String>newArrayList();
    private final List<Long> timestampList = Lists.<Long>newArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled;

    /** Current profiling section */
    private String profilingSection = "";
    private final Map<String, Long> profilingMap = Maps.<String, Long>newHashMap();

    /**
     * Clear profiling.
     */
    public void clearProfiling()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }

    /**
     * Start section
     */
    public void startSection(String name)
    {
        if (this.profilingEnabled)
        {
            if (!this.profilingSection.isEmpty())
            {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + name;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(Long.valueOf(System.nanoTime()));
        }
    }

    public void func_194340_a(Supplier<String> p_194340_1_)
    {
        if (this.profilingEnabled)
        {
            this.startSection(p_194340_1_.get());
        }
    }

    /**
     * End section
     */
    public void endSection()
    {
        if (this.profilingEnabled)
        {
            long i = System.nanoTime();
            long j = ((Long)this.timestampList.remove(this.timestampList.size() - 1)).longValue();
            this.sectionList.remove(this.sectionList.size() - 1);
            long k = i - j;

            if (this.profilingMap.containsKey(this.profilingSection))
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(((Long)this.profilingMap.get(this.profilingSection)).longValue() + k));
            }
            else
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(k));
            }

            if (k > 100000000L)
            {
                LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", this.profilingSection, Double.valueOf((double)k / 1000000.0D));
            }

            this.profilingSection = this.sectionList.isEmpty() ? "" : (String)this.sectionList.get(this.sectionList.size() - 1);
        }
    }

    public List<Profiler.Result> getProfilingData(String profilerName)
    {
        if (!this.profilingEnabled)
        {
            return Collections.<Profiler.Result>emptyList();
        }
        else
        {
            long i = this.profilingMap.containsKey("root") ? ((Long)this.profilingMap.get("root")).longValue() : 0L;
            long j = this.profilingMap.containsKey(profilerName) ? ((Long)this.profilingMap.get(profilerName)).longValue() : -1L;
            List<Profiler.Result> list = Lists.<Profiler.Result>newArrayList();

            if (!profilerName.isEmpty())
            {
                profilerName = profilerName + ".";
            }

            long k = 0L;

            for (String s : this.profilingMap.keySet())
            {
                if (s.length() > profilerName.length() && s.startsWith(profilerName) && s.indexOf(".", profilerName.length() + 1) < 0)
                {
                    k += ((Long)this.profilingMap.get(s)).longValue();
                }
            }

            float f = (float)k;

            if (k < j)
            {
                k = j;
            }

            if (i < k)
            {
                i = k;
            }

            for (String s1 : this.profilingMap.keySet())
            {
                if (s1.length() > profilerName.length() && s1.startsWith(profilerName) && s1.indexOf(".", profilerName.length() + 1) < 0)
                {
                    long l = ((Long)this.profilingMap.get(s1)).longValue();
                    double d0 = (double)l * 100.0D / (double)k;
                    double d1 = (double)l * 100.0D / (double)i;
                    String s2 = s1.substring(profilerName.length());
                    list.add(new Profiler.Result(s2, d0, d1));
                }
            }

            for (String s3 : this.profilingMap.keySet())
            {
                this.profilingMap.put(s3, Long.valueOf(((Long)this.profilingMap.get(s3)).longValue() * 999L / 1000L));
            }

            if ((float)k > f)
            {
                list.add(new Profiler.Result("unspecified", (double)((float)k - f) * 100.0D / (double)k, (double)((float)k - f) * 100.0D / (double)i));
            }

            Collections.sort(list);
            list.add(0, new Profiler.Result(profilerName, 100.0D, (double)k * 100.0D / (double)i));
            return list;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String name)
    {
        this.endSection();
        this.startSection(name);
    }

    public void func_194339_b(Supplier<String> p_194339_1_)
    {
        this.endSection();
        this.func_194340_a(p_194339_1_);
    }

    public String getNameOfLastSection()
    {
        return this.sectionList.isEmpty() ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }

    public static final class Result implements Comparable<Profiler.Result>
    {
        public double usePercentage;
        public double totalUsePercentage;
        public String profilerName;

        public Result(String profilerName, double usePercentage, double totalUsePercentage)
        {
            this.profilerName = profilerName;
            this.usePercentage = usePercentage;
            this.totalUsePercentage = totalUsePercentage;
        }

        public int compareTo(Profiler.Result p_compareTo_1_)
        {
            if (p_compareTo_1_.usePercentage < this.usePercentage)
            {
                return -1;
            }
            else
            {
                return p_compareTo_1_.usePercentage > this.usePercentage ? 1 : p_compareTo_1_.profilerName.compareTo(this.profilerName);
            }
        }

        public int getColor()
        {
            return (this.profilerName.hashCode() & 11184810) + 4473924;
        }
    }
}
