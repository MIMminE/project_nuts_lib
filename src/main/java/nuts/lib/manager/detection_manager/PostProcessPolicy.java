package nuts.lib.manager.detection_manager;

import java.util.List;

public interface PostProcessPolicy<T> {

    void process(List<T> processingTarget);
}
