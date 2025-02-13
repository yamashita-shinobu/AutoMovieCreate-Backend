select
  userid,
  video_id,
  '' as pc_video,
  '' as mobile_video
from
  mstknr.create_videos
where
  userid = /* userId */''