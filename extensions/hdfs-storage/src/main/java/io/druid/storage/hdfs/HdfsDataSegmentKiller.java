/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.druid.storage.hdfs;

import com.google.inject.Inject;
import io.druid.segment.loading.DataSegmentKiller;
import io.druid.segment.loading.SegmentLoadingException;
import io.druid.timeline.DataSegment;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class HdfsDataSegmentKiller implements DataSegmentKiller
{
  private final Configuration config;

  @Inject
  public HdfsDataSegmentKiller(final Configuration config)
  {
    this.config = config;
  }

  @Override
  public void kill(DataSegment segment) throws SegmentLoadingException
  {
    final Path path = getPath(segment);
    final FileSystem fs = checkPathAndGetFilesystem(path);
    try {
      if (path.getName().endsWith(".zip")) {

        // path format -- > .../dataSource/interval/version/partitionNum/xxx.zip
        Path partitionNumDir = path.getParent();
        if (!fs.delete(partitionNumDir, true)) {
          throw new SegmentLoadingException(
              "Unable to kill segment, failed to delete dir [%s]",
              partitionNumDir.toString()
          );
        }

        //try to delete other directories if possible
        Path versionDir = partitionNumDir.getParent();
        if (safeNonRecursiveDelete(fs, versionDir)) {
          Path intervalDir = versionDir.getParent();
          if (safeNonRecursiveDelete(fs, intervalDir)) {
            Path dataSourceDir = intervalDir.getParent();
            safeNonRecursiveDelete(fs, dataSourceDir);
          }
        }
      } else {
        throw new SegmentLoadingException("Unknown file type[%s]", path);
      }
    }
    catch (IOException e) {
      throw new SegmentLoadingException(e, "Unable to kill segment");
    }
  }

  private boolean safeNonRecursiveDelete(FileSystem fs, Path path)
  {
    try {
      return fs.delete(path, false);
    }
    catch (Exception ex) {
      return false;
    }
  }

  private Path getPath(DataSegment segment)
  {
    return new Path(String.valueOf(segment.getLoadSpec().get("path")));
  }

  private FileSystem checkPathAndGetFilesystem(Path path) throws SegmentLoadingException
  {
    FileSystem fs;
    try {
      fs = path.getFileSystem(config);

      if (!fs.exists(path)) {
        throw new SegmentLoadingException("Path[%s] doesn't exist.", path);
      }

      return fs;
    }
    catch (IOException e) {
      throw new SegmentLoadingException(e, "Problems interacting with filesystem[%s].", path);
    }
  }
}
