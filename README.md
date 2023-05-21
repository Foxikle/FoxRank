# FoxRank
[![version](https://img.shields.io/badge/Release-v1.9.4-blue)](https://github.com/Foxikle/FoxRank/releases/)
[![version](https://img.shields.io/badge/Wiki_Pages-2-green)](https://github.com/Foxikle/FoxRank/wiki)

### Installation Proccess

Check out the wiki linked [Here](https://github.com/Foxikle/FoxRank/wiki).

### For Developers

FoxRank does support other plugins through the use of its API.

To get started, add the following dependency tag to your `pom.xml`

```xml

<dependency>
    <groupId>me.foxikle</groupId>
    <artifactId>foxrank</artifactId>
    <version>1.9.4</version>
</dependency>
```

This tells maven to download the FoxRank library for you.

Next, to get an Instance of FoxRank, simply use Bukkit's `ServicesManager` class.
Here is an example class using it. Make sure this is in your main class' `onEnable` method.

```java
import me.foxikle.foxrank.FoxRank;
import org.bukkit.plugin.java.JavaPlugin;

public class Demo implements JavaPlugin {

    private FoxRank foxRank;

    @Override
    onEnable() {
        foxrank = this.getServer().getServicesManager().load(FoxRank.class);
    }

    public FoxRank getFoxRank() {
        return foxRank;
    }
}
```

Now you should be all set to use the FoxRank library. If you encounter any bugs, please be sure to report them in the
issues tab.





