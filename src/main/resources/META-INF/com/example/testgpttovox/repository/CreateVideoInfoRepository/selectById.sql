select
  userid,
  video_id,
  title,
  description,
  tag,
  scenario
from
  mstknr.create_video_info
where
  userid = /* userId */''
and
  video_id = /* videoId */''